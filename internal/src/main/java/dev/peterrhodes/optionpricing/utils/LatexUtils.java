package dev.peterrhodes.optionpricing.utils;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;

/**
 * Collection of static constants and methods for building LaTeX strings.&nbsp;For more information see the <a href="https://download.nus.edu.sg/mirror/ctan/info/latex2e-help-texinfo/latex2e.html">LaTeX2e unofficial reference manual</a> or the LaTeX project <a href="https://www.latex-project.org/help/documentation/">documentation</a>:
 */
public interface LatexUtils {

    //region fields
    //----------------------------------------------------------------------

    /**
     * Math symbol for the lowercase Greek letter sigma (σ).
     */
    String MATH_SYMBOL_GREEK_LETTER_SIGMA_LOWERCASE = "\\sigma ";

    /**
     * Math symbol for the partial differential (∂).
     */
    String MATH_SYMBOL_PARTIAL_DIFFERENTIAL = "\\partial ";

    //----------------------------------------------------------------------
    //endregion fields

    //region public methods
    //----------------------------------------------------------------------

    /**
     * Produces an exponential value (eˣ).
     *
     * @param exponent exponent / index / power value, i.e.&nbsp;the value x in eˣ
     * @return the exponential
     */
    static String exponential(String exponent) {
        return superscript(romanFontStyle("e"), exponent);
    }

    /**
     * Produces a fraction.
     *
     * @param numerator top value of the fraction
     * @param denominator bottom value of the fraction
     * @return the fraction
     */
    static String fraction(String numerator, String denominator) {
        return "\\frac" + commandMandatoryArgument(numerator) + commandMandatoryArgument(denominator);
    }

    /**
     * Produces a fraction with 2 as the denominator.
     *
     * @param numerator top value of the fraction
     * @return the fraction
     */
    static String half(String numerator) {
        return fraction(numerator, "2");
    }

    /**
     * Produces a natural logarithm with the argument enclosed in parentheses.
     *
     * @param argument argument of the natural logarithm function
     * @return the natural logarithm
     */
    static String naturalLogarithm(String argument) {
        return "\\ln" + commandMandatoryArgument(subFormula(argument, LatexDelimeterType.PARENTHESIS));
    }

    /**
     * Produces a partial derivative fraction.
     *
     * @param dependentVariable top value of the fraction
     * @param independentVariable bottom value of the fraction
     * @return the partial derivative fraction
     */
    static String partialDerivative(String dependentVariable, String independentVariable) {
        return fraction(MATH_SYMBOL_PARTIAL_DIFFERENTIAL + dependentVariable, MATH_SYMBOL_PARTIAL_DIFFERENTIAL + independentVariable);
    }

    /**
     * Produces a partial derivative fraction.
     *
     * @param dependentVariable top value of the fraction
     * @param independentVariable bottom value of the fraction
     * @param order order/degree of the derivative
     * @return the partial derivative fraction
     */
    static String partialDerivative(String dependentVariable, String independentVariable, String order) {
        return fraction(superscript(MATH_SYMBOL_PARTIAL_DIFFERENTIAL, order) + dependentVariable, MATH_SYMBOL_PARTIAL_DIFFERENTIAL + superscript(independentVariable, order));
    }

    /**
     * Typeset the given letters in Roman font.
     *
     * @param letters the letters which are to be typeset
     * @return the typesetted letters
     */
    static String romanFontStyle(String letters) {
        return "\\mathrm" + commandMandatoryArgument(letters);
    }

    /**
     * Produces an nth root using the radical symbol (√).
     *
     * @param radicand the value under the root
     * @param order the order/degree of the root (n), e.g.&nbsp;the "3" in ∛
     * @return the nth root as a radical
     */
    static String root(String radicand, String order) {
        return "\\sqrt" + commandOptionalArgument(order) + commandMandatoryArgument(radicand);
    }

    /**
     * Produces a squared value (b²).
     *
     * @param base value the base, i.e.&nbsp;the value b in b²
     * @return the square value
     */
    static String squared(String base) {
        return superscript(base, "2");
    }

    /**
     * Produces a square root using the radical symbol (√).
     *
     * @param radicand the value under the root
     * @return the square root as a radical
     */
    static String squareRoot(String radicand) {
        return "\\sqrt" + commandMandatoryArgument(radicand);
    }

    /**
     * Turns the given value into a subformula, i.e.&nbsp;surrounds it with delimeters.
     * <p>
     * Note: this is also intended to be used for substituting values into an equation when the value needs to be surrounded by parentheses.
     * </p>
     *
     * @param value value to be turned into a subformula
     * @param type type of delimeter to use
     * @return the subformula
     */
    static String subFormula(String value, LatexDelimeterType type) {
        switch (type) {
            case PARENTHESIS:
                return leftRight(value, "(", ")");
            case BRACKET:
                return leftRight(value, "[", "]");
            case BRACE:
                return leftRight(value, "\\{", "\\}");
            case NONE:
            default:
                return value;
        }
    }

    /**
     * Produces a subscript (bₙ).
     *
     * @param base value the base, i.e.&nbsp;the value b in bₙ
     * @param exp subscript value, i.e.&nbsp;the value n in bₙ
     * @return the subscript
     */
    static String subscript(String base, String exp) {
        return base + "_" + commandMandatoryArgument(exp);
    }

    /**
     * Produces a superscript (bⁿ).
     *
     * @param base value the base, i.e.&nbsp;the value b in bⁿ
     * @param exp superscript value, i.e.&nbsp;the value n in bⁿ
     * @return the superscript
     */
    static String superscript(String base, String exp) {
        return base + "^" + commandMandatoryArgument(exp);
    }

    //----------------------------------------------------------------------
    //endregion public methods

    //region private methods
    //----------------------------------------------------------------------

    private static String commandMandatoryArgument(String argument) {
        return "{" + argument + "}";
    }

    private static String commandOptionalArgument(String argument) {
        return "[" + argument + "]";
    }

    private static String leftRight(String value, String leftDelimeter, String rightDelimeter) {
        return "\\left" + leftDelimeter + " " + value + " \\right" + rightDelimeter;
    }

    //----------------------------------------------------------------------
    //endregion private methods
}
