package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.internal.common.PublicCloneable;
import dev.peterrhodes.optionpricing.internal.utils.CopyUtils;

/**
 * Model for the details of an option price calculation performed with the <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a> model.
 */
public final class CoxRossRubinstein {

    private int timeSteps;
    private double deltat;
    private double u;
    private double d;
    private double p;
    private Node[] nodes;

    /**
     * Creates a model for the details of the Cox, Ross, and Rubinstein option price calculation.
     *
     * @param timeSteps Number of time steps in the calculation.
     * @param deltat (Δt) length of a single time interval/step.
     * @param u Proportional up movement.
     * @param d Proportional down movement.
     * @param p Probability of an up movement (the corresponding probability of a down movement is {@code 1 - p}).
     * @param nodes List of the tree nodes used to perform the calculation.
     */
    public CoxRossRubinstein(int timeSteps, double deltat, double u, double d, double p, Node[] nodes) {
        this.timeSteps = timeSteps;
        this.deltat = deltat;
        this.u = u;
        this.d = d;
        this.p = p;
        this.nodes = CopyUtils.deepCopy(nodes, CoxRossRubinstein.Node.class);
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
    public Node[] getNodes() {
        return CopyUtils.deepCopy(this.nodes, CoxRossRubinstein.Node.class);
    }

    //----------------------------------------------------------------------
    //endregion getters

    /**
     * Represents an individual node in the binomial tree.
     */
    public static class Node implements PublicCloneable<Node> {

        private int i;
        private int j;
        private double S;
        private double V;
        private boolean exercised;

        /**
         * Creates a node in the tree.
         *
         * @param i Time step position (starts from zero at t = 0).
         * @param j Asset price position for the given time step (starts from zero, goes from lowest to highest asset price).
         * @param S Asset price at the node.
         * @param V Value of the option at the node.
         * @param exercised Flag indicating whether the option was exercised at the node or not.
         */
        public Node(int i, int j, double S, double V, boolean exercised) {
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
        public Node clone() {
            try {
                return (Node) super.clone();
            } catch (CloneNotSupportedException e) {
                return new Node(this.i, this.j, this.S, this.V, this.exercised);
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
}
