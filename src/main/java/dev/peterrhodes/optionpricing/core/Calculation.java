package dev.peterrhodes.optionpricing.core;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

/**
 * Describes the details of a mathematical calculation.
 */
@Getter
public class Calculation {

    /**
     * The inputs used to solve the equations used in the calculation.
     */
    @Getter(value = AccessLevel.NONE)
    private List<EquationInput> inputs;

    /**
     * Calculation steps written in LaTeX.
     */
    @Getter(value = AccessLevel.NONE)
    private List<String> steps;

    /**
     * Final answer of the calculation.
     */
    private double answer;

    /**
     * Creates an object that represents a mathematical calculation.
     *
     * @param inputs The inputs used to solve the equations used in the calculation.
     * @param steps Calculation steps written in LaTeX.
     * @param answer Final answer of the calculation.
     */
    public Calculation(List<EquationInput> inputs, List<String> steps, double answer) {
        // Deep copy inputs
        this.inputs = new ArrayList();
        for (EquationInput input : inputs) {
            this.inputs.add((EquationInput) input.clone());
        }

        // Deep copy steps
        this.steps = new ArrayList();
        this.steps.addAll(steps);

        this.answer = answer;
    }

    /**
     * Returns a deep copy of the equation inputs list.
     *
     * @return equation inputs
     */
    public List<EquationInput> getInputs() {
        List<EquationInput> clone = new ArrayList();
        for (EquationInput input : this.inputs) {
            clone.add((EquationInput) input.clone());
        }
        return clone;
    }

    /**
     * Returns a deep copy of the steps list.
     *
     * @return steps
     */
    public List<String> getSteps() {
        List<String> deepCopy = new ArrayList();
        deepCopy.addAll(this.steps);
        return deepCopy;
    }
}
