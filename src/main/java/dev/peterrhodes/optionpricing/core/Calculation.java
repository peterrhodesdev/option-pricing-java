package dev.peterrhodes.optionpricing.core;

import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class Calculation {

    private List<EquationInput> inputs;
    private List<String> steps;
    private String answer;

    /**
     * TODO.
     */
    public Calculation(List<EquationInput> inputs, List<String> steps, String answer) {
        this.inputs = inputs;
        this.steps = steps;
        this.answer = answer;
    }
}
