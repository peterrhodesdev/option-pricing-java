package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.LatticeNode;
import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class CoxRossRubinsteinPricer {

    private static void checkParameters(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
    }

    /**
     * Calculates the price of the given option.
     * @param option the option to perform the calculation for
     * @param timeSteps number of time steps in the tree
     * @return the calculated price of the option
     * @throws IllegalArgumentException if timeSteps is not greater than zero
     */
    public static double price(Option option, int timeSteps) throws IllegalArgumentException {
        checkParameters(timeSteps);
        CoxRossRubinsteinModel model = performCalculation(option, timeSteps);
        return model.getPrice();
    }

    //region calculation
    //----------------------------------------------------------------------

    /**
     * Performs all of the calculations necessary to populate a {@link dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel CoxRossRubinsteinModel}, i.e. calculates the option price along with a list of the tree nodes.
     * @param option the option to perform the calculation for
     * @param timeSteps number of time steps in the tree
     * @return model object populated with the results of the calculations
     * @throws IllegalArgumentException if timeSteps is not greater than zero
     */
    public static CoxRossRubinsteinModel calculation(Option option, int timeSteps) throws IllegalArgumentException {
        checkParameters(timeSteps);
        return performCalculation(option, timeSteps);
    }

    private static CoxRossRubinsteinModel performCalculation(Option option, int timeSteps) {
        CoxRossRubinsteinModel model = determineModelParameters(option, timeSteps);
        double Δt = model.getDeltat(), u = model.getU(), d = model.getD(), p = model.getP();

        List<LatticeNode> nodes = new ArrayList();

        // Create the tree
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int n = 0; n <= i; n++) { // nth node at the ith time step (from lowest underlying price to highest)
                LatticeNode node = createNode(option.getS(), i, n, u, d);
                nodes.add(node);
            }
        }

        // Working backwards through the tree calculating the option values
        for (int i = timeSteps; i >= 0; i--) {
            for (int n = 0; n <= i; n++) {
                calculateNodeOptionValue(option, timeSteps, nodes, i, n, Δt, p);
            }
        }

        model.setOutputs(nodes.get(0).getV(), nodes);
        return model;
    }

    private static CoxRossRubinsteinModel determineModelParameters(Option option, int timeSteps) {
        CoxRossRubinsteinModel model = new CoxRossRubinsteinModel(option, timeSteps);
        
        double Δt = option.getT() / timeSteps; // length of a single time interval/step
        double u = Math.exp(option.getV() * Math.sqrt(Δt)); // proportional up movement
        double d = Math.exp(-option.getV() * Math.sqrt(Δt)); // proportional down movement
        double a = Math.exp((option.getR() - option.getQ()) * Δt); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)

        model.setParameters(Δt, u, d, p);
        return model;
    }

    private static LatticeNode createNode(double S_0, int i, int n, double u, double d) {
        // underlying price = S₀uⁿdⁱ⁻ⁿ
        double S = S_0 * Math.pow(u, n) * Math.pow(d, i - n);
        //double t = i == this.timeSteps ? option.getT() : i * Δt;
        double V = 0; // Can't calculate yet

        LatticeNode node = new LatticeNode(i, n, S, V, false);
        return node;
    }

    private static void calculateNodeOptionValue(Option option, int timeSteps, List<LatticeNode> nodes, int i, int n, double Δt, double p) {
        int currentIndex = calculateNodeIndex(i, n);
        LatticeNode currentNode = nodes.get(currentIndex);
        double S = currentNode.getS();

        double V;

        if (i == timeSteps) {
            double exerciseValue = calculateOptionExerciseValue(option, S);
            V = Math.max(0.0, exerciseValue);
            currentNode.setExercised(exerciseValue > 0);
        } else {
            int downIndex = currentIndex + (i + 1);
            int upIndex = downIndex + 1;
            double optionCurrentValue = (p * nodes.get(upIndex).getV() + (1 - p) * nodes.get(downIndex).getV()) * Math.exp(-option.getR() * Δt);
            double earlyExerciseValue;
            switch (option.getStyle()) {
                case AMERICAN:
                    earlyExerciseValue = Math.max(0.0, calculateOptionExerciseValue(option, S));
                    break;
                case EUROPEAN:
                default:
                    earlyExerciseValue = 0.0;
            }
            V = Math.max(optionCurrentValue, earlyExerciseValue);
            currentNode.setExercised(earlyExerciseValue > optionCurrentValue);
        }

        currentNode.setV(V);
    }

    /**
     * The values of n (left) and the list index (right) for a 3-step binomial tree:
     *           3              9
     *          /              /
     *         2              5
     *        / \            / \
     *       1   2          2   8
     *      / \ /          / \ /
     *     0   1          0   4
     *      \ / \          \ / \
     *       0   1          1   7
     *        \ /            \ /
     *         0              3
     *          \              \
     *           0              6
     * i = 0 1 2 3   Σi = 0 1 3 6
     */
    private static int calculateNodeIndex(int i, int n) {
        return IntStream.rangeClosed(0, i).sum() + n;
    }

    private static double calculateOptionExerciseValue(Option option, double S) {
        return option.getType() == OptionType.CALL ? S - option.getK() : option.getK() - S;
    }

    //----------------------------------------------------------------------
    //endregion
}
