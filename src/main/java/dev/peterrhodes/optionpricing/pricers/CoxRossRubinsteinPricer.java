package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.common.LatticeNode;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.options.AbstractOption;
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
     * Creates a new Cox, Ross, and Rubinstein option pricer.
     *
     * @param timeSteps Number of time steps in the tree.
     * @throws IllegalArgumentException if {@code timeSteps} is not greater than zero
     */
    public CoxRossRubinsteinPricer(int timeSteps) throws IllegalArgumentException {
        checkParameters(timeSteps);
        this.timeSteps = timeSteps;
    }

    /**
     * Calculates the price of the given option.
     *
     * @param option the option to perform the calculation for
     * @return the calculated price of the option
     * @throws NullPointerException if {@code option} is null
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
     * @throws NullPointerException if {@code option} is null
     */
    public CoxRossRubinsteinModel calculation(@NonNull T option) throws NullPointerException {
        return performCalculation(option);
    }

    //region perform calculation
    //----------------------------------------------------------------------

    private CoxRossRubinsteinModel performCalculation(T option) {
        double S_0 = option.getSpotPrice();
        double τ = option.getTimeToMaturity();
        double σ = option.getVolatility();
        double r = option.getRiskFreeRate();
        double q = option.getDividendYield();

        double[] modelParameters = determineModelParameters(τ, σ, r, q);
        double Δt = modelParameters[0];
        double u = modelParameters[1];
        double d = modelParameters[2];
        double p = modelParameters[3];

        List<LatticeNode> nodes = new ArrayList();

        // Create the tree
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int j = 0; j <= i; j++) { // jth node at the ith time step (from lowest underlying price to highest)
                LatticeNode node = createNode(S_0, i, j, u, d);
                nodes.add(node);
            }
        }

        // Working backwards through the tree calculating the option values
        for (int i = timeSteps; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                calculateNodeOptionValue(option, r, nodes, i, j, Δt, p);
            }
        }

        return new CoxRossRubinsteinModel(this.timeSteps, Δt, u, d, p, nodes, nodes.get(0).getV());
    }

    private double[] determineModelParameters(double τ, double σ, double r, double q) {
        double Δt = τ / (double) this.timeSteps; // length of a single time interval/step
        double u = Math.exp(σ * Math.sqrt(Δt)); // proportional up movement
        double d = Math.exp(-σ * Math.sqrt(Δt)); // proportional down movement
        double a = Math.exp((r - q) * Δt); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)

        return new double[] { Δt, u, d, p };
    }

    private LatticeNode createNode(double S_0, int i, int j, double u, double d) {
        double S = S_0 * Math.pow(u, j) * Math.pow(d, i - j); // S_ij = S₀ u^j d^(i-j)
        //double t = i == this.timeSteps ? option.getT() : i * Δt;
        double V = 0; // Can't calculate yet

        LatticeNode node = new LatticeNode(i, j, S, V, false);
        return node;
    }

    private void calculateNodeOptionValue(T option, double r, List<LatticeNode> nodes, int i, int j, double Δt, double p) {
        int currentIndex = calculateNodeIndex(i, j);
        LatticeNode currentNode = nodes.get(currentIndex);
        double S_ij = currentNode.getS();

        double V;

        if (i == this.timeSteps) {
            V = option.exerciseValue(S_ij);
            currentNode.setExercised(V > 0);
        } else {
            int downIndex = currentIndex + (i + 1);
            int upIndex = downIndex + 1;
            double optionCurrentValue = (p * nodes.get(upIndex).getV() + (1 - p) * nodes.get(downIndex).getV()) * Math.exp(-r * Δt);
            double earlyExerciseValue;
            switch (option.getStyle()) {
                case AMERICAN:
                    earlyExerciseValue = option.exerciseValue(S_ij);
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

    //----------------------------------------------------------------------
    //endregion perform calculation

    private void checkParameters(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
    }
}
