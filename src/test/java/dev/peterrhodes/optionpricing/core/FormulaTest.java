package dev.peterrhodes.optionpricing.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #Formula}.
 */
class FormulaTest {

    //region build formula tests
    //----------------------------------------------------------------------

    @Test
    void Build_formula_with_simplified_rhs_not_set() {
        // Arrange
        Formula formula = new Formula("x", "y");

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = y");
    }

    @Test
    void Build_formula_with_simplified_rhs_null() {
        // Arrange
        Formula formula = new Formula("x", "y", null);

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = y");
    }

    @Test
    void Build_formula_with_simplified_rhs_set() {
        // Arrange
        Formula formula = new Formula("x", "y + y", "2 y");

        // Act
        String result = formula.build();

        // Assert
        assertThat(result).isEqualTo("x = y + y = 2 y");
    }

    //----------------------------------------------------------------------
    //endregion build formula tests
}
