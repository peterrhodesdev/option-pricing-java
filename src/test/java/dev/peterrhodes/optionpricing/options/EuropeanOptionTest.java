package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

class EuropeanOptionTest {

    private final String greaterThanZeroMessage = "must be greater than zero";

    /* IllegalArgumentException */

    @Test
    void zeroSpotPrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 0.0, 100.0, 1.0, 0.25, 0.1);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void zeroStrikePrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 0.0, 1.0, 0.25, 0.1);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void zeroTimeToExpiration() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 0.0, 0.25, 0.1);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void zeroVolatility() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.0, 0.1);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    /*
     * Calculations
     * Values checked with https://www.wolframalpha.com/input/?i=black+scholes
     */

    /* call, no dividend */

    @Test
    void calculation_call_noDividend_1() {
        // Arrange
        EuropeanOption euro = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.25, 0.1);

        // Act
        double result = euro.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(14.98, withPrecision(0.01));
    }

    /* put, no dividend */

    @Test
    void calculation_put_noDividend_1() {
        // Arrange
        EuropeanOption euro = new EuropeanOption(OptionType.PUT, 100.0, 100.0, 1.0, 0.25, 0.1);

        // Act
        double result = euro.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(5.46, withPrecision(0.01));
    }
}
