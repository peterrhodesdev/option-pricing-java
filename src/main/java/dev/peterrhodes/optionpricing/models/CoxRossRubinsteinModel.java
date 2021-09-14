package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.core.LatticeNode;
import dev.peterrhodes.optionpricing.core.Option;
import java.util.List;
import lombok.Getter;

/**
 * Model for the results of calculation performed with {@link dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer CoxRossRubinsteinPricer}.
 */
@Getter
public class CoxRossRubinsteinModel {

    // Inputs
    private Option option;
    private int timeSteps;

    // Parameters
    private double deltat;
    private double u;
    private double d;
    private double p;

    // Outputs
    private double price;
    private List<LatticeNode> nodes;

    /**
     * Creates a model for the results of a calculation performed by {@link dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer CoxRossRubinsteinPricer}.
     *
     * @param option the option that the calculation was performed for
     * @param timeSteps number of time steps in the calculation
     */
    public CoxRossRubinsteinModel(Option option, int timeSteps) {
        this.option = option;
        this.timeSteps = timeSteps;
    }

    /**
     * Set the parameters of the model.
     *
     * @param deltat (Δt) length of a single time interval/step
     * @param u proportional up movement
     * @param d proportional down movement
     * @param p probability of an up movement (the corresponding probability of a down movement is 1 - p)
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
     * @param price calculated price of the option
     * @param nodes list of the lattice nodes used to perform the calculation
     */
    public void setOutputs(double price, List<LatticeNode> nodes) {
        this.price = price;
        this.nodes = nodes;
    }
}
