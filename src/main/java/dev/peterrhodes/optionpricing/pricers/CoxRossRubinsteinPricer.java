package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.AbstractOption;
import dev.peterrhodes.optionpricing.core.LatticeNode;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Implements the binomial options pricing model described by <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a>.
 */
@Getter
@Setter
public class CoxRossRubinsteinPricer<T extends AbstractOption> {

    /**
     * Number of time steps in the tree.
     */
    private int timeSteps;

    /**
     * TODO.
     *
     * @param timeSteps Number of time steps in the tree.
     */
    public CoxRossRubinsteinPricer(int timeSteps) throws IllegalArgumentException, NullPointerException {
        checkParameters(timeSteps);
        this.timeSteps = timeSteps;
    }

    /**
     * Calculates the price of the given option.
     *
     * @param option the option to perform the calculation for
     * @return the calculated price of the option
     * TODO
     */
    public double price(@NonNull T option) throws NullPointerException {
        CoxRossRubinsteinModel model = performCalculation(option);
        return model.getPrice();
    }

    /**
     * Performs all of the calculations necessary to populate a {@link dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel CoxRossRubinsteinModel}, i.e.&nbsp;calculates the option price along with a list of the tree nodes.
     *
     * @param option the option to perform the calculation for
     * @return model object populated with the results of the calculations
     * TODO
     */
    public CoxRossRubinsteinModel calculation(@NonNull T option) throws NullPointerException {
        return performCalculation(option);
    }

    //region perform calculation
    //----------------------------------------------------------------------

    private CoxRossRubinsteinModel performCalculation(T option) {
        CoxRossRubinsteinModel model = determineModelParameters(option);

        List<LatticeNode> nodes = new ArrayList();

        // Create the tree
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int j = 0; j <= i; j++) { // jth node at the ith time step (from lowest underlying price to highest)
                LatticeNode node = createNode(option, i, j, model.getU(), model.getD());
                nodes.add(node);
            }
        }

        // Working backwards through the tree calculating the option values
        for (int i = timeSteps; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                calculateNodeOptionValue(option, nodes, i, j, model.getDeltat(), model.getP());
            }
        }

        model.setOutputs(nodes.get(0).getV(), nodes);
        return model;
    }

    private CoxRossRubinsteinModel determineModelParameters(T option) {
        CoxRossRubinsteinModel model = new CoxRossRubinsteinModel(this.timeSteps);
        
        double deltat = option.getT() / timeSteps; // (Δt) length of a single time interval/step
        double u = Math.exp(option.getVol() * Math.sqrt(deltat)); // proportional up movement
        double d = Math.exp(-option.getVol() * Math.sqrt(deltat)); // proportional down movement
        double a = Math.exp((option.getR() - option.getQ()) * deltat); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)

        model.setParameters(deltat, u, d, p);
        return model;
    }

    private LatticeNode createNode(T option, int i, int j, double u, double d) {
        double S = option.getS() * Math.pow(u, j) * Math.pow(d, i - j); // S_ij = S_0 u^j d^(i-j)
        //double t = i == this.timeSteps ? option.getT() : i * Δt;
        double V = 0; // Can't calculate yet

        LatticeNode node = new LatticeNode(i, j, S, V, false);
        return node;
    }

    private void calculateNodeOptionValue(T option, List<LatticeNode> nodes, int i, int j, double deltat, double p) {
        int currentIndex = calculateNodeIndex(i, j);
        LatticeNode currentNode = nodes.get(currentIndex);
        double S = currentNode.getS();

        double V;

        if (i == this.timeSteps) {
            double exerciseValue = calculateOptionExerciseValue(option, S);
            V = Math.max(0.0, exerciseValue);
            currentNode.setExercised(exerciseValue > 0);
        } else {
            int downIndex = currentIndex + (i + 1);
            int upIndex = downIndex + 1;
            double optionCurrentValue = (p * nodes.get(upIndex).getV() + (1 - p) * nodes.get(downIndex).getV()) * Math.exp(-option.getR() * deltat);
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
     * The tree is flattened into a one-dimensional list, so we need to determine the corresponding list index for a given node (i, j).&nbsp;The list index is calculated as the sum of the i's plus j.&nbsp;Below shows the values of j (left) and the list index (right) for a 3-step binomial tree.
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
    private int calculateNodeIndex(int i, int j) {
        return IntStream.rangeClosed(0, i).sum() + j;
    }

    private double calculateOptionExerciseValue(T option, double S) {
        return option.getType() == OptionType.CALL ? S - option.getK() : option.getK() - S;
    }

    //----------------------------------------------------------------------
    //endregion perform calculation

    private void checkParameters(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
    }
}
