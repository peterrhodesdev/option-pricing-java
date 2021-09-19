package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.core.LatticeNode;
import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.utils.CopyUtils;
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
     *
     * @param price Calculated price of the option.
     * @param nodes List of the lattice nodes used to perform the calculation.
     */
    public void setOutputs(double price, List<LatticeNode> nodes) {
        this.price = price;
        this.nodes = CopyUtils.deepCopy(nodes);
    }

    /**
     * Returns the lattice nodes list.
     *
     * @return lattice nodes
     */
    public List<LatticeNode> getNodes() {
        return CopyUtils.deepCopy(this.nodes);
    }
}
