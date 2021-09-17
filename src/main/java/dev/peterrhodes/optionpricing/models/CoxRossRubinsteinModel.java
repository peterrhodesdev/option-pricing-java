package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.core.LatticeNode;
import dev.peterrhodes.optionpricing.core.Option;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Model that represents a calculation performed with {@link dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer CoxRossRubinsteinPricer}.
 */
@Getter
public class CoxRossRubinsteinModel {

    // Inputs

    /**
     * The option that the calculation was performed for.
     */
    private Option option;

    /**
     * Number of time steps in the calculation.
     */
    private int timeSteps;

    // Parameters

    /**
     * (Δt) length of a single time interval/step.
     */
    private double deltat;

    /**
     * Proportional up movement.
     */
    private double u;

    /**
     * Proportional down movement.
     */
    private double d;

    /**
     * Probability of an up movement (the corresponding probability of a down movement is {@code 1 - p}).
     */
    private double p;

    // Outputs

    /**
     * Calculated price of the option.
     */
    private double price;

    /**
     * List of the lattice nodes used to perform the calculation.
     */
    @Getter(value = AccessLevel.NONE)
    private List<LatticeNode> nodes;

    /**
     * Creates a model for the results of a calculation performed by {@link dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer CoxRossRubinsteinPricer}.
     *
     * @param option The option that the calculation was performed for.
     * @param timeSteps Number of time steps in the calculation.
     */
    public CoxRossRubinsteinModel(Option option, int timeSteps) {
        this.option = option;
        this.timeSteps = timeSteps;
    }

    /**
     * Set the parameters of the model.
     *
     * @param deltat (Δt) length of a single time interval/step.
     * @param u Proportional up movement.
     * @param d Proportional down movement.
     * @param p Probability of an up movement (the corresponding probability of a down movement is {@code 1 - p}).
     */
    public void setParameters(double deltat, double u, double d, double p) {
        this.deltat = deltat;
        this.u = u;
        this.d = d;
        this.p = p;
    }

    /**
     * Set the outputs of the model.
     * <p>
     * Note: a deep copy of the nodes list is made, i.e. making changes to the original object won't affect this copy.
     * </p>
     *
     * @param price Calculated price of the option.
     * @param nodes List of the lattice nodes used to perform the calculation.
     */
    public void setOutputs(double price, List<LatticeNode> nodes) {
        this.price = price;

        // Deep copy nodes
        this.nodes = new ArrayList();
        for (LatticeNode node : nodes) {
            this.nodes.add((LatticeNode) node.clone());
        }
    }

    /**
     * Returns a deep copy of the lattice nodes list.
     *
     * @return lattice nodes
     */
    public List<LatticeNode> getNodes() {
        List<LatticeNode> clone = new ArrayList();
        for (LatticeNode node : this.nodes) {
            clone.add((LatticeNode) node.clone());
        }
        return clone;
    }
}
