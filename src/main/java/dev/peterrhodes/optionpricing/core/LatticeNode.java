package dev.peterrhodes.optionpricing.core;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an individual node of a pricing model lattice.
 */
@Getter
@Setter
public class LatticeNode {

    private int i;
    private int n;
    private double S;
    private double V;
    private boolean exercised;

    /**
     * Creates a node in the pricing model lattice.
     * @param i time step position (starts from zero at t = 0)
     * @param n asset price position for the given time step (starts from zero, goes from lowest to highest asset price)
     * @param S asset price
     * @param V value of the option
     * @param exercised flag indicating whether the option was exercised or not
     */
    public LatticeNode(int i, int n, double S, double V, boolean exercised) {
        this.i = i;
        this.n = n;
        this.S = S;
        this.V = V;
        this.exercised = exercised;
    }
}
