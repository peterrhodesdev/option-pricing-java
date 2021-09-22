package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.common.LatticeNode;
import dev.peterrhodes.optionpricing.utils.CopyUtils;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Model that represents a calculation performed with {@link dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer}.
 */
@Getter
public class CoxRossRubinsteinModel {

    /**
     * Number of time steps in the calculation.
     */
    private int timeSteps;

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

    /**
     * List of the lattice nodes used to perform the calculation.
     */
    @Getter(value = AccessLevel.NONE)
    private List<LatticeNode> nodes;

    /**
     * Calculated price of the option.
     */
    private double price;

    /**
     * Creates a model for the results of a calculation performed by {@link dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer}.
     *
     * @param timeSteps Number of time steps in the calculation.
     * @param deltat (Δt) length of a single time interval/step.
     * @param u Proportional up movement.
     * @param d Proportional down movement.
     * @param p Probability of an up movement (the corresponding probability of a down movement is {@code 1 - p}).
     * @param nodes List of the lattice nodes used to perform the calculation.
     * @param price Calculated price of the option.
     */
    public CoxRossRubinsteinModel(int timeSteps, double deltat, double u, double d, double p, List<LatticeNode> nodes, double price) {
        this.timeSteps = timeSteps;
        this.deltat = deltat;
        this.u = u;
        this.d = d;
        this.p = p;
        this.nodes = CopyUtils.deepCopy(nodes);
        this.price = price;
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
