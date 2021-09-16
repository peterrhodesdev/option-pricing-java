package dev.peterrhodes.optionpricing.helpers;

import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.enums.BracketType;
import java.util.List;

/**
 * TODO.
 */
public class CalculationHelper {

    private CalculationHelper() {}

    /**
     * TODO.
     */
    public static String substituteValuesIntoEquation(String equation, List<EquationInput> inputs) {
        String substitutedValues = equation;

        for (EquationInput input : inputs) {
            String key = input.getKey();
            String regEx;
            if (key.matches("^\\w+")) { // key is a word
                regEx = "\\b" + key + "\\b"; // match whole word only
            //} else if (key.indexOf("\\") == 0) { // key is a LaTex command, e.g. \sigma
            } else {
                //regEx = key.replaceAll("\\\\", "\\\\\\\\");
                regEx = key.replaceAll("[\\W]", "\\\\$0");
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
     * TODO.
     */
    public static String solveFormula(Formula formula, List<EquationInput> inputs, String answer) {
        String rhsEquation = (formula.getRhsSimplified() != null ? formula.getRhsSimplified() : formula.getRhs());
        String substituted = substituteValuesIntoEquation(rhsEquation, inputs);

        // TODO refactor " = "
        return formula.getLhs() + " = " + rhsEquation + " = " + substituted + (answer != null ? " = " + answer : "");
    }
}
