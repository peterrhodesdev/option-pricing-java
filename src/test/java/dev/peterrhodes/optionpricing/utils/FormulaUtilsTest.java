package dev.peterrhodes.optionpricing.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.peterrhodes.optionpricing.common.EquationInput;
import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #FormulaUtils}.
 */
public class FormulaUtilsTest {

    //region substitute tests
    //----------------------------------------------------------------------

    @Test
    public void Empty_equation_should_throw() {
        // Arrange
        String equation = "";
        EquationInput[] values = { new EquationInput.Builder("x").withNumberValue(0).build() };

        // Act Assert
        assertThatThrownBy(() -> {
            String ex = FormulaUtils.substitute(equation, values);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("blank/empty");
    }

    @Test
    public void Empty_values_should_throw() {
        // Arrange
        String equation = "x";
        EquationInput[] values = {};

        // Act Assert
        assertThatThrownBy(() -> {
            String ex = FormulaUtils.substitute(equation, values);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("empty");
    }

    @Test
    public void Substituting_with_unmatched_input_shouldnt_change_equation() {
        // Arrange
        String equation = "x";
        EquationInput[] values = { new EquationInput.Builder("y").withNumberValue(1.23).build() };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Test
    public void Substitution_shouldnt_match_substrings() {
        // Arrange
        String equation = "xx ax xa 2x x2 x_ _x x_x";
        EquationInput[] values = { new EquationInput.Builder("x").withNumberValue(1.23).build() };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Test
    public void Substituting_multiple_values() {
        // Arrange
        String equation = "a 2 ab (abc) [d] {de} def^2 2^a ab+abc-d=de def";
        EquationInput[] values = {
            new EquationInput.Builder("a").withStringValue("0.00").build(),
            new EquationInput.Builder("ab").withNumberValue(1).build(),
            new EquationInput.Builder("abc").withNumberValue(230).build(),
            new EquationInput.Builder("d").withNumberValue(45).build(),
            new EquationInput.Builder("de").withNumberValue(6.7).build(),
            new EquationInput.Builder("def").withNumberValue(89).build()
        };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo("0.00 2 1 (230) [45] {6.7} 89^2 2^0.00 1+230-45=6.7 89");
    }

    @Test
    public void Substituting_a_latex_command() {
        // Arrange
        String equation = "\\sigma 2 \\sigma (\\sigma) [\\sigma] {\\sigma} \\sigma^2 2^\\sigma \\sigma+\\sigma-\\sigma=\\sigma \\sigma";
        EquationInput[] values = { new EquationInput.Builder("\\sigma").withNumberValue(1.23).build() };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo("1.23 2 1.23 (1.23) [1.23] {1.23} 1.23^2 2^1.23 1.23+1.23-1.23=1.23 1.23");
    }

    @Test
    public void Substitution_with_special_characters() {
        // Arrange
        String equation = "f(a) \\left( b \\right) \\frac{c}{d} e+f-g^h=i";
        EquationInput[] values = {
            new EquationInput.Builder("f(a)").withNumberValue(1).build(),
            new EquationInput.Builder("\\left( b \\right)").withNumberValue(2).build(),
            new EquationInput.Builder("\\frac{c}{d}").withNumberValue(3).build(),
            new EquationInput.Builder("e+f-g^h=i").withNumberValue(4).build()
        };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo("1 2 3 4");
    }

    @Test
    public void Substituting_with_brackets() {
        // Arrange
        String equation = "a b c";
        EquationInput[] values = {
            new EquationInput.Builder("a").withNumberValue(1).withDelimeter(LatexDelimeterType.PARENTHESIS).build(),
            new EquationInput.Builder("b").withNumberValue(2).withDelimeter(LatexDelimeterType.BRACKET).build(),
            new EquationInput.Builder("c").withNumberValue(3).withDelimeter(LatexDelimeterType.BRACE).build()
        };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo("\\left( 1 \\right) \\left[ 2 \\right] \\left\\{ 3 \\right\\}");
    }

    //----------------------------------------------------------------------
    //endregion substitute tests

    //region solve tests
    //----------------------------------------------------------------------

    @Test
    public void Empty_formula_should_throw() {
        // Arrange
        String[] formula = {};
        EquationInput[] values = {};
        String answer = null;

        // Act Assert
        assertThatThrownBy(() -> {
            String[] ex = FormulaUtils.solve(formula, values, answer);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("empty");
    }

    // TODO

    //----------------------------------------------------------------------
    //endregion solve tests

    //region disabled tests
    //----------------------------------------------------------------------

    @Disabled("Unlikely scenario, determine whether needs to be fixed")
    @Test
    public void Latex_command_text_key_shouldnt_substitute_for_latex_command() {
        // Arrange
        String equation = "\\x \\x \\x";
        EquationInput[] values = { new EquationInput.Builder("x").withNumberValue(1.23).build() };

        // Act
        String result = FormulaUtils.substitute(equation, values);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Disabled("Not intending to support alpha substitution, numeric substitution doesn't match intended usage")
    @Test
    public void Values_shouldnt_override_each_other() {
        // Arrange
        String alphaEquation = "x + y";
        EquationInput[] alphaValues = {
            new EquationInput.Builder("x").withStringValue("y").build(),
            new EquationInput.Builder("y").withStringValue("x").build()
        };
        String numericEquation = "1 + 2";
        EquationInput[] numericValues = {
            new EquationInput.Builder("1").withNumberValue(2).build(),
            new EquationInput.Builder("y").withNumberValue(1).build()
        };

        // Act
        String alphaResult = FormulaUtils.substitute(alphaEquation, alphaValues);
        String numericResult = FormulaUtils.substitute(numericEquation, numericValues);

        // Assert
        assertThat(alphaResult).isEqualTo("y + x");
        assertThat(numericResult).isEqualTo("2 + 1");
    }

    //----------------------------------------------------------------------
    //endregion disabled tests
}
