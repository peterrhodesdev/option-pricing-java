package dev.peterrhodes.optionpricing.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #Formula}.
 */
class FormulaTest {

    //region build formula tests
    //----------------------------------------------------------------------

    @Test
    void Build_formula_with_steps_not_set() {
        // Arrange
        Formula formula = new Formula("x", "y");

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = y");
    }

    @Test
    void Build_formula_with_empty_steps() {
        // Arrange
        Formula formula = new Formula("x", "y", new ArrayList<String>());

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = y");
    }

    @Test
    void Build_formula_with_single_step() {
        // Arrange
        List<String> steps = new ArrayList();
        steps.add("y + y");
        Formula formula = new Formula("x", "2 y", steps);

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = y + y = 2 y");
    }

    @Test
    void Build_formula_with_multiple_steps() {
        // Arrange
        List<String> steps = new ArrayList();
        steps.add("2(y + y)");
        steps.add("2(2 y)");
        Formula formula = new Formula("x", "4 y", steps);

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = 2(y + y) = 2(2 y) = 4 y");
    }


    //----------------------------------------------------------------------
    //endregion build formula tests
}
