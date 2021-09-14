package dev.peterrhodes.optionpricing.core;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an individual node of a pricing model lattice.
 */
@Getter
@Setter
public class LatticeNode implements Cloneable {

    private int i;
    private int j;
    private double S;
    private double V;
    private boolean exercised;

    /**
     * Creates a node in the pricing model lattice.
     *
     * @param i time step position (starts from zero at t = 0)
     * @param j asset price position for the given time step (starts from zero, goes from lowest to highest asset price)
     * @param S asset price
     * @param V value of the option
     * @param exercised flag indicating whether the option was exercised or not
     */
    public LatticeNode(int i, int j, double S, double V, boolean exercised) {
        this.i = i;
        this.j = j;
        this.S = S;
        this.V = V;
        this.exercised = exercised;
    }

    /**
     * Clone the object.
     */
    @Override
    public Object clone() {
        try {
            return (LatticeNode) super.clone();
        } catch (CloneNotSupportedException e) {
            return new LatticeNode(this.i, this.j, this.S, this.V, this.exercised);
        }
    }
}
