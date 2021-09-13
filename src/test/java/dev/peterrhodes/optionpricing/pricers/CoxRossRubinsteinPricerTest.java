package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.options.EuropeanOption;
import dev.peterrhodes.optionpricing.options.ExoticOption;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

class CoxRossRubinsteinPricerTest {

    private final String greaterThanZeroMessage = "must be greater than zero";

    //region IllegalArgumentException
    //----------------------------------------------------------------------

    @Test
    void zeroTimeSteps() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);

        // Act Assert
        assertThatThrownBy(() -> {
            double ex = CoxRossRubinsteinPricer.price(option, 0);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    //----------------------------------------------------------------------
    //endregion

    //region price
    //----------------------------------------------------------------------

    /*
     * Hull: page 311, section 13.9, Figure 13.10
     */
    @Test
    void price_Hull_Fig13_10() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 50, 52, 2, 0.3, 0.05, 0);

        // Act
        double result = CoxRossRubinsteinPricer.price(option, 2);

        // Assert
        assertThat(result).isEqualTo(7.43, withPrecision(0.01));
    }

    /*
     * Hull: page 313, section 13.11, Example 13.1
     */
    @Test
    void price_Hull_Ex13_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);

        // Act
        double result = CoxRossRubinsteinPricer.price(option, 2);

        // Assert
        assertThat(result).isEqualTo(53.39, withPrecision(0.01));
    }

    /*
     * Hull: page 314, section 13.11, Example 13.2
     */
    @Test
    void price_Hull_Ex13_2() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.CALL, 0.6100, 0.6000, 0.25, 0.12, 0.05, 0.07);

        // Act
        double result = CoxRossRubinsteinPricer.price(option, 3);

        // Assert
        assertThat(result).isEqualTo(0.019, withPrecision(0.001));
    }

    //----------------------------------------------------------------------
    //endregion
}
