package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.internal.common.PublicCloneable;

/**
 * Represents an individual node of a pricing model lattice.
 */
public class LatticeNode implements PublicCloneable<LatticeNode> {

    private int i;
    private int j;
    private double S;
    private double V;
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

    //region getters
    //----------------------------------------------------------------------

    /**
     * Get i.
     *
     * @return i
     */
    public int getI() {
        return this.i;
    }

    /**
     * Get j.
     *
     * @return j
     */
    public int getJ() {
        return this.j;
    }

    /**
     * Get S.
     *
     * @return S
     */
    public double getS() {
        return this.S;
    }

    /**
     * Get V.
     *
     * @return V
     */
    public double getV() {
        return this.V;
    }

    /**
     * Get exercised.
     *
     * @return exercised
     */
    public boolean getExercised() {
        return this.exercised;
    }

    //----------------------------------------------------------------------
    //endregion getters

    //region setters
    //----------------------------------------------------------------------

    /**
     * Set exercised.
     */
    public void setExercised(boolean exercised) {
        this.exercised = exercised;
    }

    /**
     * Set V.
     */
    public void setV(double V) {
        this.V = V;
    }

    //----------------------------------------------------------------------
    //endregion setters
}
