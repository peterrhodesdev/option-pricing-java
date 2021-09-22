package dev.peterrhodes.optionpricing.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents an individual node of a pricing model lattice.
 */
@Getter
@Setter
public class LatticeNode implements PublicCloneable<LatticeNode> {

    /**
     * Time step position (starts from zero at t = 0).
     */
    private int i;

    /**
     * Asset price position for the given time step (starts from zero, goes from lowest to highest asset price).
     */
    private int j;

    /**
     * Asset price at the node.
     */
    private double S;

    /**
     * Value of the option at the node.
     */
    private double V;

    /**
     * Flag indicating whether the option was exercised at the node or not.
     */
    private boolean exercised;

    /**
     * Creates a node in a pricing model lattice.
     *
     * @param i Time step position (starts from zero at t = 0).
     * @param j Asset price position for the given time step (starts from zero, goes from lowest to highest asset price).
     * @param S Asset price at the node.
     * @param V Value of the option at the node.
     * @param exercised Flag indicating whether the option was exercised at the node or not.
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
     *
     * @return the cloned object
     */
    @Override
    public LatticeNode clone() {
        try {
            return (LatticeNode) super.clone();
        } catch (CloneNotSupportedException e) {
            return new LatticeNode(this.i, this.j, this.S, this.V, this.exercised);
        }
    }
}
