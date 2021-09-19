package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.utils.CopyUtils;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class CalculationModel {

    /**
     * TODO.
     */
    @Getter(value = AccessLevel.NONE)
    private String[][] steps;

    /**
     * TODO.
     */
    private double answer;

    /**
     * TODO.
     */
    public CalculationModel(String[][] steps, double answer) {
        this.steps = CopyUtils.deepCopy(steps);
        this.answer = answer;
    }

    /**
     * TODO.
     */
    public String[][] getSteps() {
        return CopyUtils.deepCopy(this.steps);
    }
}
