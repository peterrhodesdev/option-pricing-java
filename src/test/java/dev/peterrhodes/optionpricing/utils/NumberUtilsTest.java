package dev.peterrhodes.optionpricing.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import dev.peterrhodes.optionpricing.enums.PrecisionType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #NumberUtils}.
 */
class NumberUtilsTest {

    @Test
    void Null_precision_type_should_throw() {
        // Arrange
        Integer digits = 1;
        PrecisionType precisionType = null;
        Number number = 1;

        // Act Assert
        assertThatThrownBy(() -> {
            String ex = NumberUtils.precision(number, digits, precisionType);
        }).isInstanceOf(NullPointerException.class)
          .hasMessageContaining("null");
    }

    @Test
    void Precision_type_isnt_unchanged_with_invalid_digits_should_throw() {
        // Arrange
        Integer digitsNull = null;
        Integer digitsLessThanZero = null;
        PrecisionType precisionType = PrecisionType.DECIMAL_PLACES;
        Number number = 1;

        // Act Assert
        Class expectedEx = IllegalStateException.class;

        assertThatThrownBy(() -> {
            String ex = NumberUtils.precision(number, digitsNull, precisionType);
        }).isInstanceOf(expectedEx)
          .hasMessageContaining("null");

        assertThatThrownBy(() -> {
            String ex = NumberUtils.precision(number, digitsLessThanZero, precisionType);
        }).isInstanceOf(expectedEx)
          .hasMessageContaining("zero");
    }

    @Test
    void Precision_type_unchanged_shouldnt_change_number() {
        // Arrange
        Integer digits = null;
        PrecisionType precisionType = PrecisionType.UNCHANGED;
        Number[] numbers = { 10, 1, 1.0, 1.4, 1.5, 1.6, -10, -1, -1.0, -1.4, -1.5, -1.6 };

        // Act
        List<String> result = new ArrayList<String>();
        for (Number number : numbers) {
            result.add(NumberUtils.precision(number, digits, precisionType));
        }

        // Assert
        String[] expected = { "10", "1", "1.0", "1.4", "1.5", "1.6", "-10", "-1", "-1.0", "-1.4", "-1.5", "-1.6" };
        this.performAsserts(numbers, result, expected);
    }

    @Test
    void Rounding_algorithm_is_half_up() {
        // Arrange
        Integer digits = 0;
        Number[] numbers = { 1.5, -1.5 };

        // Act
        List<String> resultDecimalPlaces = new ArrayList<String>();
        for (Number number : numbers) {
            resultDecimalPlaces.add(NumberUtils.precision(number, digits, PrecisionType.DECIMAL_PLACES));
        }

        List<String> resultSignificantFigures = new ArrayList<String>();
        for (Number number : numbers) {
            resultSignificantFigures.add(NumberUtils.precision(number, digits, PrecisionType.SIGNIFICANT_FIGURES));
        }

        // Assert
        String[] expected = { "2", "-2" };
        this.performAsserts(numbers, resultDecimalPlaces, expected);
        this.performAsserts(numbers, resultSignificantFigures, expected);
    }

    @Test
    void Two_decimal_places() {
        // Arrange
        Integer digits = 2;
        PrecisionType precisionType = PrecisionType.DECIMAL_PLACES;
        Number[] numbers = { 46, 4.6, 0.46, 0.046, 0.0046 };

        // Act
        List<String> result = new ArrayList<String>();
        for (Number number : numbers) {
            result.add(NumberUtils.precision(number, digits, precisionType));
        }

        // Assert
        String[] expected = { "46.00", "4.60", "0.46", "0.05", "0.00" };
        this.performAsserts(numbers, result, expected);
    }

    @Test
    void Two_significant_figures() {
        // Arrange
        Integer digits = 2;
        PrecisionType precisionType = PrecisionType.SIGNIFICANT_FIGURES;
        Number[] numbers = { 0, 0.1, 0.12, 0.124, 0.126, 1, 1.2, 1.20, 1.24, 1.26, 12, 12.0, 12.4, 12.6, 120, 124, 126, 123.456 };

        // Act
        List<String> result = new ArrayList<String>();
        for (Number number : numbers) {
            result.add(NumberUtils.precision(number, digits, precisionType));
        }

        // Assert
        String[] expected = { "0.0", "0.10", "0.12", "0.12", "0.13", "1.0", "1.2", "1.2", "1.2", "1.3", "12", "12", "12", "13", "120", "120", "130", "120" };
        this.performAsserts(numbers, result, expected);
    }

    //region private methods
    //----------------------------------------------------------------------

    private void performAsserts(Number[] numbers, List<String> result, String[] expected) {
        assertThat(result.size())
            .as("size")
            .isEqualTo(expected.length);

        for (int i = 0; i < expected.length; i++) {
            assertThat(result.get(i))
                .as(String.format("rounding %s", numbers[i].toString()))
                .isEqualTo(expected[i]);
        }
    }

    //----------------------------------------------------------------------
    //endregion private methods
}
