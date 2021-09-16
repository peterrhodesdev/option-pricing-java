package dev.peterrhodes.optionpricing.helpers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.enums.BracketType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CalculationHelper}.
 */
class CalculationHelperTest {

    private final String greaterThanZeroMessage = "must be greater than zero";

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
        inputs.add(new EquationInput("y", "123"));

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
        inputs.add(new EquationInput("x", "123"));

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
        inputs.add(new EquationInput("x", "123"));

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
        inputs.add(new EquationInput("a", "0.00"));
        inputs.add(new EquationInput("ab", "1"));
        inputs.add(new EquationInput("abc", "230"));
        inputs.add(new EquationInput("d", "045"));
        inputs.add(new EquationInput("de", "6.7"));
        inputs.add(new EquationInput("def", "89"));

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo("0.00 2 1 (230) [045] {6.7} 89^2 2^0.00 1+230-045=6.7 89");
    }

    @Test
    void Substituting_a_latex_command() {
        // Arrange
        String equation = "\\sigma 2 \\sigma (\\sigma) [\\sigma] {\\sigma} \\sigma^2 2^\\sigma \\sigma+\\sigma-\\sigma=\\sigma \\sigma";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("\\sigma", "123"));

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo("123 2 123 (123) [123] {123} 123^2 2^123 123+123-123=123 123");
    }

    @Test
    void Substitution_with_special_characters() {
        // Arrange
        String equation = "f(a) \\left( b \\right) \\frac{c}{d} e+f-g^h=i";
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("f(a)", "1"));
        inputs.add(new EquationInput("\\left( b \\right)", "2"));
        inputs.add(new EquationInput("\\frac{c}{d}", "3"));
        inputs.add(new EquationInput("e+f-g^h=i", "4"));

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
        inputs.add(new EquationInput("a", "1", BracketType.ROUND));
        inputs.add(new EquationInput("b", "2", BracketType.SQUARE));
        inputs.add(new EquationInput("c", "3", BracketType.CURLY));

        // Act
        String result = CalculationHelper.substituteValuesIntoEquation(equation, inputs);

        // Assert
        assertThat(result).isEqualTo(" \\left( 1 \\right)   \\left[ 2 \\right]   \\left{ 3 \\right} ");
    }

/*
    @Test
    void solveSingleInputNoAnswerRhsSimplified() {
        // Arrange
        Formula formula = new Formula("x", "y + y", "2 y");
        Map<String, Double> inputs = new HashMap();
        inputs.put("y", 1.0);

        // Act
        String result = formula.solve(inputs, null);

        // Assert
        assertThat(result).isEqualTo("x = 2 y = 2 (1.0)");
    }
*/
    //----------------------------------------------------------------------
    //endregion substitution tests

    //region formula solving tests
    //----------------------------------------------------------------------

    @Test
    void Solve_formula_without_a_simplified_rhs_and_null_answer() {
        // Arrange
        Formula formula = new Formula("x", "y + y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("y", "1"));
        String answer = null;

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = y + y = 1 + 1");
    }

    @Test
    void Solve_formula_with_a_null_simplified_rhs_and_null_answer() {
        // Arrange
        Formula formula = new Formula("x", "y + y", null);
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("y", "1"));
        String answer = null;

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = y + y = 1 + 1");
    }

    @Test
    void Solve_formula_without_a_simplified_rhs_with_an_answer() {
        // Arrange
        Formula formula = new Formula("x", "y + y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("y", "1"));
        String answer = "2";

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = y + y = 1 + 1 = 2");
    }

    @Test
    void Solve_formula_with_a_simplified_rhs_and_an_answer() {
        // Arrange
        Formula formula = new Formula("x", "y + y", "2 y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("y", "1"));
        String answer = "2";

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = 2 y = 2 1 = 2");
    }

    @Test
    void Solve_formula_with_a_simplified_rhs_brackets_and_an_answer() {
        // Arrange
        Formula formula = new Formula("x", "y + y", "2 y");
        List<EquationInput> inputs = new ArrayList();
        inputs.add(new EquationInput("y", "1", BracketType.ROUND));
        String answer = "2";

        // Act
        String result = CalculationHelper.solveFormula(formula, inputs, answer);

        // Assert
        assertThat(result).isEqualTo("x = 2 y = 2  \\left( 1 \\right)  = 2");
    }

    //----------------------------------------------------------------------
    //endregion substitution tests

}
