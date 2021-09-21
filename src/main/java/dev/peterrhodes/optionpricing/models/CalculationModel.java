package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.utils.CopyUtils;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Model of a mathematical calculation.
 */
@Getter
public class CalculationModel {

    /**
     * The steps of the calculation written as LaTeX mathematical expressions.&nbsp;Each step is split into parts based on the equals sign.
     */
    @Getter(value = AccessLevel.NONE)
    private String[][] steps;

    /**
     * The final answer of the calculation.
     */
    private double answer;

    /**
     * Creates a model for the results of a mathematical calculation.
     */
    public CalculationModel(String[][] steps, double answer) {
        this.steps = CopyUtils.deepCopy(steps);
        this.answer = answer;
    }

    /**
     * Gets the steps of the calculation.
     */
    public String[][] getSteps() {
        return CopyUtils.deepCopy(this.steps);
    }
}
