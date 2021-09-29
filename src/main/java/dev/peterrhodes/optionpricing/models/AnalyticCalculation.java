package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.internal.utils.CopyUtils;

/**
 * Model of an analytic option calculation.
 */
public final class AnalyticCalculation {

    private String[][] steps;

    /**
     * Creates a model for the results of a analytic option calculation.
     *
     * @param steps The steps of the calculation written as LaTeX mathematical expressions.&nbsp;Each step is split into parts based on the equals sign.
     */
    public AnalyticCalculation(String[][] steps) {
        this.steps = CopyUtils.deepCopy(steps);
    }

    //region getters
    //----------------------------------------------------------------------

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
