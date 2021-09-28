package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.internal.utils.CopyUtils;

/**
 * Model of an analytic option calculation.
 */
public class AnalyticCalculation {

    private String[][] steps;
    private double answer;

    /**
     * Creates a model for the results of a analytic option calculation.
     *
     * @param steps The steps of the calculation written as LaTeX mathematical expressions.&nbsp;Each step is split into parts based on the equals sign.
     * @param answer The final answer of the calculation.
     */
    public AnalyticCalculation(String[][] steps, double answer) {
        this.steps = CopyUtils.deepCopy(steps);
        this.answer = answer;
    }

    //region getters
    //----------------------------------------------------------------------

    /**
     * Get answer.
     *
     * @return answer
     */
    public double getAnswer() {
        return this.answer;
    }

    /**
     * Get steps.
     *
     * @return steps
     */
    public String[][] getSteps() {
        return CopyUtils.deepCopy(this.steps);
    }

    //----------------------------------------------------------------------
    //endregion getters
}
