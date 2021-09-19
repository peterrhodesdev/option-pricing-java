package dev.peterrhodes.optionpricing.models;

import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class CalculationModel {

    /**
     * TODO.
     */
    private String[][] steps;

    /**
     * TODO.
     */
    private double answer;

    /**
     * TODO.
     */
    public CalculationModel(String[][] steps, double answer) {
        this.steps = steps;
        this.answer = answer;
    }
}
