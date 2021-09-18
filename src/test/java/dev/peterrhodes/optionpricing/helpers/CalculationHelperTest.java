package dev.peterrhodes.optionpricing.helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #CalculationHelper}.
 */
class CalculationHelperTest {

    //region substitution tests
    //----------------------------------------------------------------------

    @Test
    void Substituting_with_empty_inputs_shouldnt_change_equation() {
        // Arrange
        String equation = "x";
        List<EquationInput> inputs = new ArrayList();

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Test
    void Substituting_with_unmatched_input_shouldnt_change_equation() {
        // Arrange
        String equation = "x";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("y").withNumberValue(1.23).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Test
    void Substitution_shouldnt_match_substrings() {
        // Arrange
        String equation = "xx ax xa 2x x2 x_ _x x_x";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("x").withNumberValue(1.23).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Disabled("Unlikely scenario, determine whether needs to be fixed")
    @Test
    void Substitution_edge_case_where_key_is_latex_command() {
        // Arrange
        String equation = "\\x \\x \\x";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("x").withNumberValue(1.23).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo(equation);
    }

    @Test
    void Substituting_multiple_inputs() {
        // Arrange
        String equation = "a 2 ab (abc) [d] {de} def^2 2^a ab+abc-d=de def";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("a").withStringValue("0.00").build());
        inputs.add(new EquationInput.Builder("ab").withNumberValue(1).build());
        inputs.add(new EquationInput.Builder("abc").withNumberValue(230).build());
        inputs.add(new EquationInput.Builder("d").withNumberValue(45).build());
        inputs.add(new EquationInput.Builder("de").withNumberValue(6.7).build());
        inputs.add(new EquationInput.Builder("def").withNumberValue(89).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo("0.00 2 1 (230) [45] {6.7} 89^2 2^0.00 1+230-45=6.7 89");
    }

    @Test
    void Substituting_a_latex_command() {
        // Arrange
        String equation = "\\sigma 2 \\sigma (\\sigma) [\\sigma] {\\sigma} \\sigma^2 2^\\sigma \\sigma+\\sigma-\\sigma=\\sigma \\sigma";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("\\sigma").withNumberValue(1.23).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo("1.23 2 1.23 (1.23) [1.23] {1.23} 1.23^2 2^1.23 1.23+1.23-1.23=1.23 1.23");
    }

    @Test
    void Substitution_with_special_characters() {
        // Arrange
        String equation = "f(a) \\left( b \\right) \\frac{c}{d} e+f-g^h=i";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("f(a)").withNumberValue(1).build());
        inputs.add(new EquationInput.Builder("\\left( b \\right)").withNumberValue(2).build());
        inputs.add(new EquationInput.Builder("\\frac{c}{d}").withNumberValue(3).build());
        inputs.add(new EquationInput.Builder("e+f-g^h=i").withNumberValue(4).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo("1 2 3 4");
    }

    @Test
    void Substituting_with_brackets() {
        // Arrange
        String equation = "a b c";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("a").withNumberValue(1).withDelimeter(LatexDelimeterType.PARENTHESIS).build());
        inputs.add(new EquationInput.Builder("b").withNumberValue(2).withDelimeter(LatexDelimeterType.BRACKET).build());
        inputs.add(new EquationInput.Builder("c").withNumberValue(3).withDelimeter(LatexDelimeterType.BRACE).build());

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo("\\left( 1 \\right) \\left[ 2 \\right] \\left\\{ 3 \\right\\}");
    }

    //----------------------------------------------------------------------
    //endregion substitution tests

    //region formula solving tests
    //----------------------------------------------------------------------

    @Test
    void Solve_formula_with_null_answer() {
        // Arrange
        Formula formula = new Formula("x", "y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("y").withNumberValue(1).build());
        String answer = null;

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = y = 1");
    }

    @Test
    void Solve_formula_with_an_answer() {
        // Arrange
        Formula formula = new Formula("x", "y + y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("y").withNumberValue(1).build());
        String answer = "2";

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = y + y = 1 + 1 = 2");
    }

    @Test
    void Solve_formula_with_an_answer_using_brackets() {
        // Arrange
        Formula formula = new Formula("x", "2 y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput.Builder("y").withNumberValue(1).withDelimeter(LatexDelimeterType.PARENTHESIS).build());
        String answer = "2";

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = 2 y = 2 \\left( 1 \\right) = 2");
    }

    //----------------------------------------------------------------------
    //endregion substitution tests
}
