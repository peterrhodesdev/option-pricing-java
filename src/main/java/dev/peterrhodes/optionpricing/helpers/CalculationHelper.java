package dev.peterrhodes.optionpricing.helpers;

import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.enums.BracketType;
import java.util.List;

/**
 * Collection of helper methods for building calculations.
 */
public class CalculationHelper {

    private CalculationHelper() {}

    /**
     * Substitutes the given inputs into the equation, i.e.&nbsp;replaces the variables with their values.
     *
     * @param equation LaTeX equation
     * @param inputs list of inputs identifying the variable and the value to be substituted in for it
     * @return LaTeX equation with the variables substituted in
     */
    public static String substituteValuesIntoEquation(String equation, List<EquationInput> inputs) {
        String substitutedValues = equation;

        for (EquationInput input : inputs) {
            String key = input.getKey();
            String regEx;
            if (key.matches("^\\w+")) { // key is a word
                regEx = "\\b" + key + "\\b"; // match whole word only
            } else {
                regEx = key.replaceAll("[\\W]", "\\\\$0"); // escape all non-word characters for the regex
            }
            String value = addBrackets(input.getValue(), input.getBracketType());
            substitutedValues = substitutedValues.replaceAll(regEx, value);
        }
        
        return substitutedValues;
    }

    private static String addBrackets(String value, BracketType bracketType) {
        String bracketedValue;

        switch (bracketType) {
            case ROUND:
                bracketedValue = " \\\\left( " + value + " \\\\right) ";
                break;
            case SQUARE:
                bracketedValue = " \\\\left[ " + value + " \\\\right] ";
                break;
            case CURLY:
                bracketedValue = " \\\\left{ " + value + " \\\\right} ";
                break;
            case NONE:
            default:
                bracketedValue = value;
        }

        return bracketedValue;
    }

    /**
     * Solves a formula for the given inputs and answer.
     *
     * @param formula the formula object to be solved for
     * @param inputs list of inputs identifying the variable and the value to be substituted in for it
     * @param answer solution to the formula with values substituted in, or {@code null} if not required (for the case when the formulas RHS consists of a single term)
     * @return LaTeX equation representing the solution to the formula
     */
    public static String solveFormula(Formula formula, List<EquationInput> inputs, String answer) {
        String rhsEquation = (formula.getRhsSimplified() != null ? formula.getRhsSimplified() : formula.getRhs());
        String substituted = substituteValuesIntoEquation(rhsEquation, inputs);

        // TODO refactor " = "
        return formula.getLhs() + " = " + rhsEquation + " = " + substituted + (answer != null ? " = " + answer : "");
    }
}
