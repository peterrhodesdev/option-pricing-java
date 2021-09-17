package dev.peterrhodes.optionpricing.helpers;

import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Collection of static methods for building calculations.
 */
public interface CalculationHelper {

    /**
     * Substitutes the given inputs into the equation, i.e.&nbsp;replaces the variables with their values.
     *
     * @param equation LaTeX equation
     * @param inputs list of inputs identifying the variable and the value to be substituted in for it
     * @return LaTeX equation with the variables substituted in
     */
    static String substituteValuesIntoEquation(String equation, List<EquationInput> inputs) {
        String substitutedValues = equation;

        for (EquationInput input : inputs) {
            String key = input.getKey();
            String regEx;
            if (key.matches("^\\w+")) { // key is a word
                regEx = "\\b" + key + "\\b"; // match whole word only
            } else {
                regEx = key.replaceAll("[\\W]", "\\\\$0"); // escape all non-word characters for the regex
            }
            String value = LatexHelper.subFormula(input.getValue(), input.getLatexDelimeterType());
            value = value.replaceAll("\\\\", "\\\\\\\\");
            substitutedValues = substitutedValues.replaceAll(regEx, value);
        }
        
        return substitutedValues;
    }

    /**
     * Solves a formula for the given inputs and answer.
     *
     * @param formula the formula object to be solved for
     * @param inputs list of inputs identifying the variable and the value to be substituted in for it
     * @param answer solution to the formula with values substituted in, or {@code null} if not required (for the case when the formulas RHS consists of a single term)
     * @return LaTeX equation representing the solution to the formula
     */
    static String solveFormula(Formula formula, List<EquationInput> inputs, String answer) {
        String rhsSubstituted = substituteValuesIntoEquation(formula.getRhs(), inputs);

        List<String> solutionParts = new ArrayList();
        solutionParts.add(formula.getLhs());
        solutionParts.add(formula.getRhs());
        solutionParts.add(rhsSubstituted);
        if (answer != null) {
            solutionParts.add(answer);
        }

        return solutionParts.stream()
            .collect(Collectors.joining(" = "));
    }
}
