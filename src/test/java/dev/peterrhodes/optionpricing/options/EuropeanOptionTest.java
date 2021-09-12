package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

class EuropeanOptionTest {

    private final String greaterThanZeroMessage = "must be greater than zero";
    private final double pricePrecision = 0.01;
    private final double greekPrecision = 0.001;

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
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.25, 0.1, 0.05);

        // Act
        double result = option.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(11.73, withPrecision(this.pricePrecision));
    }

    /* put */

    @Test
    void analyticalPrice_put_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.PUT, 100.0, 100.0, 1.0, 0.25, 0.1, 0.05);

        // Act
        double result = option.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(7.10, withPrecision(this.pricePrecision));
    }

    @Test
    void analyticalPrice_put_2() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.PUT, 95.0, 105.0, 2.0, 0.2, 0.05, 0.15);

        // Act
        double result = option.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(26.33, withPrecision(this.pricePrecision));
    }

    /* analyticalCalculation */

    @Test
    void analyticalCalculation_call_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.25, 0.1, 0.05);

        // Act
        AnalyticalCalculation result = option.analyticalCalculation();

        // Assert
        assertThat(result.getPrice()).as("price").isEqualTo(11.73, withPrecision(this.pricePrecision));
        assertThat(result.getDelta()).as("delta").isEqualTo(0.597, withPrecision(this.greekPrecision));
        assertThat(result.getGamma()).as("gamma").isEqualTo(0.014, withPrecision(this.greekPrecision));
        assertThat(result.getVega()).as("vega").isEqualTo(35.996, withPrecision(this.greekPrecision));
        assertThat(result.getTheta()).as("theta").isEqualTo(-6.310, withPrecision(this.greekPrecision));
        assertThat(result.getRho()).as("rho").isEqualTo(47.947, withPrecision(this.greekPrecision));
    }
}
