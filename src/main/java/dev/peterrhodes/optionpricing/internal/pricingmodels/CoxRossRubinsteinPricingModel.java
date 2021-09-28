package dev.peterrhodes.optionpricing.internal.pricingmodels;

import dev.peterrhodes.optionpricing.ExerciseValueParameter;
import dev.peterrhodes.optionpricing.Option;
import dev.peterrhodes.optionpricing.PricingModel;
import dev.peterrhodes.optionpricing.internal.utils.ValidationUtils;
import dev.peterrhodes.optionpricing.models.CoxRossRubinstein;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Binomial options pricing model described by <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a>.
 */
public class CoxRossRubinsteinPricingModel implements PricingModel<CoxRossRubinstein> {

    private int timeSteps;

    // Math notation
    private double S_0;
    private double τ;
    private double σ;
    private double r;
    private double q;

    /**
     * Creates a new Cox, Ross, and Rubinstein option pricing model.
     *
     * @param timeSteps Number of time steps in the tree.
     * @throws IllegalArgumentException if {@code timeSteps} is not greater than zero
     */
    public CoxRossRubinsteinPricingModel(int timeSteps) throws IllegalArgumentException {
        ValidationUtils.checkGreaterThanZero(timeSteps, "timeSteps");
        this.timeSteps = timeSteps;
    }

    /**
     * TODO.
     *
     * @param option the option to perform the calculation on
     * @throws NullPointerException if {@code option} is null
     */
    @Override
    public double price(Option option) throws NullPointerException {
        CoxRossRubinstein calc = this.calculation(option);
        return calc.getNodes()[0].getV();
    }

    /**
     * Returns the details of the Cox, Ross, and Rubinstein calculation.
     * TODO
     *
     * @return calculation details
     */
    @Override
    public CoxRossRubinstein calculation(Option option) throws NullPointerException {
        ValidationUtils.checkNotNull(option, "option");
        this.setMathNotation(option);

        double[] modelParameters = this.determineModelParameters();
        double Δt = modelParameters[0];
        double u = modelParameters[1];
        double d = modelParameters[2];
        double p = modelParameters[3];

        List<CoxRossRubinstein.Node> nodes = new ArrayList<CoxRossRubinstein.Node>();

        // Create the tree
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int j = 0; j <= i; j++) { // jth node at the ith time step (from lowest underlying price to highest)
                CoxRossRubinstein.Node node = this.createNode(i, j, u, d);
                nodes.add(node);
            }
        }

        // Working backwards through the tree calculating the option values
        for (int i = timeSteps; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                calculateNodeOptionValue(nodes, i, j, Δt, p, option);
            }
        }

        return new CoxRossRubinstein(this.timeSteps, Δt, u, d, p, nodes.toArray(new CoxRossRubinstein.Node[0]));
    }

    private void setMathNotation(Option option) {
        this.S_0 = option.contract().initialSpotPrice().doubleValue();
        this.τ = option.contract().timeToMaturity().doubleValue();
        this.σ = option.contract().volatility().doubleValue();
        this.r = option.contract().riskFreeRate().doubleValue();
        this.q = option.contract().dividendYield().doubleValue();
    }

    private double[] determineModelParameters() {
        double Δt = this.τ / (double) this.timeSteps; // length of a single time interval/step
        double u = Math.exp(this.σ * Math.sqrt(Δt)); // proportional up movement
        double d = Math.exp(-this.σ * Math.sqrt(Δt)); // proportional down movement
        double a = Math.exp((this.r - this.q) * Δt); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)

        return new double[] { Δt, u, d, p };
    }

    private CoxRossRubinstein.Node createNode(int i, int j, double u, double d) {
        double S = this.S_0 * Math.pow(u, j) * Math.pow(d, i - j); // S_ij = S₀ u^j d^(i-j)
        double V = 0; // Can't calculate yet

        CoxRossRubinstein.Node node = new CoxRossRubinstein.Node(i, j, S, V, false);
        return node;
    }

    private void calculateNodeOptionValue(List<CoxRossRubinstein.Node> nodes, int i, int j, double Δt, double p, Option option) {
        int currentIndex = calculateNodeIndex(i, j);
        CoxRossRubinstein.Node currentNode = nodes.get(currentIndex);
        double S_ij = currentNode.getS();
        double t_i = i == this.timeSteps ? this.τ : i * Δt;
        double exerciseValue = this.calculateExerciseValue(t_i, S_ij, option);

        double V;

        if (i == this.timeSteps) {
            V = exerciseValue;
            currentNode.setExercised(V > 0);
        } else {
            int downIndex = currentIndex + (i + 1);
            int upIndex = downIndex + 1;
            double optionCurrentValue = (p * nodes.get(upIndex).getV() + (1 - p) * nodes.get(downIndex).getV()) * Math.exp(-this.r * Δt);
            double earlyExerciseValue = exerciseValue;
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

    private double calculateExerciseValue(double t_i, double S_ij, Option option) {
        ExerciseValueParameter exerciseValueParameter = new ExerciseValueParameter.Builder(t_i, S_ij)
            .build();
        return option.contract().exerciseValue(exerciseValueParameter);
    }
}
