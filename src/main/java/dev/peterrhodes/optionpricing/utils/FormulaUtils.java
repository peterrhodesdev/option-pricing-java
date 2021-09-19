package dev.peterrhodes.optionpricing.utils;

import dev.peterrhodes.optionpricing.core.EquationInput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Collection of static methods for building calculations.
 */
public interface FormulaUtils {

    /**
     * Substitute values into an equation, i.e.&nbsp;replaces the variables with their values.
     *
     * @param equation the equation with variables
     * @param values list of values for the variables in the equation
     * @return the substituted equation
     * @throws IllegalArgumentException if the equation is blank/empty, or the values array is empty
     */
    static String substitute(String equation, EquationInput[] values) {
        if (equation.trim().length() == 0) {
            throw new IllegalArgumentException("equation can't be blank/empty");
        }
        if (values.length == 0) {
            throw new IllegalArgumentException("values array can't be empty");
        }

        String substitutedEquation = equation;
        for (EquationInput value : values) {
            substitutedEquation = substitute(substitutedEquation, value);
        }
        
        return substitutedEquation;
    }

    private static String substitute(String equation, EquationInput equationInput) {
        String key = equationInput.getKey();

        String regex;
        if (key.matches("^\\w+")) { // key is a word
            regex = "\\b" + key + "\\b"; // match whole word only
        } else {
            regex = key.replaceAll("[\\W]", "\\\\$0"); // escape all non-word characters for the regex
        }

        String replacement;
        if (equationInput.hasNumberValue()) {
            replacement = NumberUtils.round(equationInput.getNumberValue(), equationInput.getPrecisionDigits(), equationInput.getRoundingMethod());
        } else {
            replacement = equationInput.getStringValue();
        }
        replacement = LatexUtils.subFormula(replacement, equationInput.getLatexDelimeterType());
        replacement = replacement.replaceAll("\\\\", "\\\\\\\\");

        return equation.replaceAll(regex, replacement);
    }

    /**
     * Returns a new array which appends TODO.
     *
     * @param formula the formula parts where the last element is the one to have the values substituted in for
     * @param values list of values for the variables in the equation
     * @param answer solution to the solved formula, or {@code null} if not required (like for the case when the formulas last element consists of a single term)
     * @return formula solution
     * @throws IllegalArgumentException if the formula array is empty
     */
    static String[] solve(String[] formula, EquationInput[] values, String answer) {
        if (formula.length == 0) {
            throw new IllegalArgumentException("formula array can't be empty");
        }

        List<String> solution = new ArrayList<String>(Arrays.asList(formula));

        if (values.length > 0) {
            String solvedEquation = substitute(formula[formula.length - 1], values);
            solution.add(solvedEquation);
        }

        if (answer != null) {
            solution.add(answer);
        }

        return solution.toArray(String[]::new);
    }
}
