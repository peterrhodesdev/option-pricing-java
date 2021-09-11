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
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 0.0, 100.0, 1.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void zeroStrikePrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 0.0, 1.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void zeroTimeToExpiration() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 0.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void zeroVolatility() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.0, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    /*
     * analyticalPrice
     *
     * Values checked with:
     * - https://www.wolframalpha.com/input/?i=black+scholes
     * - http://www.option-price.com/index.php
     */

    /* call */

    @Test
    void analyticalPrice_call_1() {
        // Arrange
        EuropeanOption euro = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.25, 0.1, 0.05);

        // Act
        double result = euro.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(11.73, withPrecision(0.01));
    }

    @Test
    void analyticalPrice_call_2() {
        // Arrange
        EuropeanOption euro = new EuropeanOption(OptionType.CALL, 95.0, 105.0, 2.0, 0.2, 0.05, 0.15);

        // Act
        double result = euro.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(1.70, withPrecision(0.01));
    }

    /* put */

    @Test
    void analyticalPrice_put_1() {
        // Arrange
        EuropeanOption euro = new EuropeanOption(OptionType.PUT, 100.0, 100.0, 1.0, 0.25, 0.1, 0.05);

        // Act
        double result = euro.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(7.10, withPrecision(0.01));
    }

    @Test
    void analyticalPrice_put_2() {
        // Arrange
        EuropeanOption euro = new EuropeanOption(OptionType.PUT, 95.0, 105.0, 2.0, 0.2, 0.05, 0.15);

        // Act
        double result = euro.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(26.33, withPrecision(0.01));
    }
}
