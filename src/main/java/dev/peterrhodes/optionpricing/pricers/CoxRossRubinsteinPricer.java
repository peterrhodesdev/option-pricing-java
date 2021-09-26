package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.Contract;
import dev.peterrhodes.optionpricing.common.ExerciseValueInput;
import dev.peterrhodes.optionpricing.common.LatticeNode;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.utils.ValidationUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Binomial options pricing model described by <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a>.
 */
public class CoxRossRubinsteinPricer implements Pricer<CoxRossRubinsteinModel> {

    private Contract contract;
    private int timeSteps;

    // Math notation
    private double S_0;
    private double τ;
    private double σ;
    private double r;
    private double q;

    /**
     * Creates a new Cox, Ross, and Rubinstein option pricer.
     *
     * @param contract the option contract to perform the calculation on
     * @param timeSteps Number of time steps in the tree.
     * @throws NullPointerException if {@code contract} is null
     * @throws IllegalArgumentException if {@code timeSteps} is not greater than zero
     */
    public CoxRossRubinsteinPricer(Contract contract, int timeSteps) throws NullPointerException, IllegalArgumentException {
        ValidationUtils.checkNotNull(contract, "contract");
        ValidationUtils.checkGreaterThanZero(timeSteps, "timeSteps");

        this.contract = contract;
        this.timeSteps = timeSteps;

        this.S_0 = contract.initialSpotPrice().doubleValue();
        this.τ = contract.timeToMaturity().doubleValue();
        this.σ = contract.volatility().doubleValue();
        this.r = contract.riskFreeRate().doubleValue();
        this.q = contract.dividendYield().doubleValue();
    }

    /**
     * Returns the details of the Cox, Ross, and Rubinstein calculation.
     *
     * @return calculation details
     */
    @Override
    public CoxRossRubinsteinModel calculation() {
        double[] modelParameters = determineModelParameters();
        double Δt = modelParameters[0];
        double u = modelParameters[1];
        double d = modelParameters[2];
        double p = modelParameters[3];

        List<LatticeNode> nodes = new ArrayList();

        // Create the tree
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int j = 0; j <= i; j++) { // jth node at the ith time step (from lowest underlying price to highest)
                LatticeNode node = createNode(i, j, u, d);
                nodes.add(node);
            }
        }

        // Working backwards through the tree calculating the option values
        for (int i = timeSteps; i >= 0; i--) {
            for (int j = 0; j <= i; j++) {
                calculateNodeOptionValue(nodes, i, j, Δt, p);
            }
        }

        double price = nodes.get(0).getV();
        return new CoxRossRubinsteinModel(price, this.timeSteps, Δt, u, d, p, nodes);
    }

    private double[] determineModelParameters() {
        double Δt = this.τ / (double) this.timeSteps; // length of a single time interval/step
        double u = Math.exp(this.σ * Math.sqrt(Δt)); // proportional up movement
        double d = Math.exp(-this.σ * Math.sqrt(Δt)); // proportional down movement
        double a = Math.exp((this.r - this.q) * Δt); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)

        return new double[] { Δt, u, d, p };
    }

    private LatticeNode createNode(int i, int j, double u, double d) {
        double S = this.S_0 * Math.pow(u, j) * Math.pow(d, i - j); // S_ij = S₀ u^j d^(i-j)
        double V = 0; // Can't calculate yet

        LatticeNode node = new LatticeNode(i, j, S, V, false);
        return node;
    }

    private void calculateNodeOptionValue(List<LatticeNode> nodes, int i, int j, double Δt, double p) {
        int currentIndex = calculateNodeIndex(i, j);
        LatticeNode currentNode = nodes.get(currentIndex);
        double S_ij = currentNode.getS();
        double t_i = i == this.timeSteps ? this.τ : i * Δt;
        double exerciseValue = this.calculateExerciseValue(t_i, S_ij);

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

    private double calculateExerciseValue(double t_i, double S_ij) {
        ExerciseValueInput exerciseValueInput = new ExerciseValueInput.Builder(t_i, S_ij)
            .build();
        return this.contract.exerciseValue(exerciseValueInput);
    }
}
