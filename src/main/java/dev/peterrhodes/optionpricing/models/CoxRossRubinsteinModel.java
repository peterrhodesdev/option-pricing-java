package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.core.Option;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CoxRossRubinsteinModel {

    @Getter
    @Setter
    public static class Node {
        private int i;
        private int n;
        private double S;
        private double V;
        private boolean exercised;
        
        /**
         * TODO
         */
        public Node(int i, int n, double S, double V, boolean exercised) {
            this.i = i;
            this.n = n;
            this.S = S;
            this.V = V;
            this.exercised = exercised;
        }
    }

    // Inputs
    private Option option;
    private int timeSteps;

    // Parameters
    private double deltat; // length of a single time interval/step
    private double u; // proportional up movement
    private double d; // proportional down movement
    private double p; // probability of an up movement (probability of a down movement is 1 - p)

    // Outputs
    private double price;
    private List<Node> nodes;

    /**
     * TODO
     */
    public CoxRossRubinsteinModel(Option option, int timeSteps) {
        this.option = option;
        this.timeSteps = timeSteps;
    }

    /**
     * TODO
     */
    public void setParameters(double Δt, double u, double d, double p) {
        this.deltat = Δt;
        this.u = u;
        this.d = d;
        this.p = p;
    }

    /**
     * TODO
     */
    public void setOutputs(double price, List<Node> nodes) {
        this.price = price;
        this.nodes = nodes;
    }
}
