package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.common.LatticeNode;
import dev.peterrhodes.optionpricing.utils.CopyUtils;
import java.util.List;

/**
 * Model for the details of an option price calculation performed with the <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a> model.
 */
public class CoxRossRubinsteinModel extends CalculationModel {

    private int timeSteps;
    private double deltat;
    private double u;
    private double d;
    private double p;
    private List<LatticeNode> nodes;

    /**
     * Creates a model for the details of the Cox, Ross, and Rubinstein option price calculation.
     *
     * @param price The price of the option.
     * @param timeSteps Number of time steps in the calculation.
     * @param deltat (Δt) length of a single time interval/step.
     * @param u Proportional up movement.
     * @param d Proportional down movement.
     * @param p Probability of an up movement (the corresponding probability of a down movement is {@code 1 - p}).
     * @param nodes List of the lattice nodes used to perform the calculation.
     */
    public CoxRossRubinsteinModel(double price, int timeSteps, double deltat, double u, double d, double p, List<LatticeNode> nodes) {
        super(price);
        this.timeSteps = timeSteps;
        this.deltat = deltat;
        this.u = u;
        this.d = d;
        this.p = p;
        this.nodes = CopyUtils.deepCopy(nodes);
    }

    //region getters
    //----------------------------------------------------------------------

    /**
     * Get timeSteps.
     *
     * @return timeSteps
     */
    public int getTimeSteps() {
        return this.timeSteps;
    }

    /**
     * Get deltat.
     *
     * @return deltat
     */
    public double getDeltat() {
        return this.deltat;
    }

    /**
     * Get u.
     *
     * @return u
     */
    public double getU() {
        return this.u;
    }

    /**
     * Get d.
     *
     * @return d
     */
    public double getD() {
        return this.d;
    }

    /**
     * Get p.
     *
     * @return p
     */
    public double getP() {
        return this.p;
    }

    /**
     * Get nodes.
     *
     * @return nodes
     */
    public List<LatticeNode> getNodes() {
        return CopyUtils.deepCopy(this.nodes);
    }

    //----------------------------------------------------------------------
    //endregion getters
}
