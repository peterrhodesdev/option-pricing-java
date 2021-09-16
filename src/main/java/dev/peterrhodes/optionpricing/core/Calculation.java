package dev.peterrhodes.optionpricing.core;

import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Describes the details of a mathematical calculation, including the input values used, intermediate calculation steps, and the final answer.
 */
@Getter
public class Calculation {

    /**
     * The inputs used to solve the equations used in the calculation.
     */
    private List<EquationInput> inputs;

    /**
     * Calculation steps written in LaTeX.
     */
    private List<String> steps;

    /**
     * Final answer of the calculation.
     */
    private String answer;

    /**
     * Creates an object that represents a mathematical calculation.
     *
     * @param inputs The inputs used to solve the equations used in the calculation.
     * @param steps Calculation steps written in LaTeX.
     * @param answer Final answer of the calculation.
     */
    public Calculation(List<EquationInput> inputs, List<String> steps, String answer) {
        this.inputs = inputs;
        this.steps = steps;
        this.answer = answer;
    }
}
