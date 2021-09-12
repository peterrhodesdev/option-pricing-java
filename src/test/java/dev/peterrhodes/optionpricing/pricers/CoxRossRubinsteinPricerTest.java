package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.options.EuropeanOption;

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
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            CoxRossRubinsteinPricer ex = new CoxRossRubinsteinPricer(0);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    //----------------------------------------------------------------------
    //endregion

    //region price
    //----------------------------------------------------------------------

    /*
     * Hull: page 313, section 13.11, Example 13.1
     */
    @Test
    void price_Hull_Ex13_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);
        CoxRossRubinsteinPricer crrPricer = new CoxRossRubinsteinPricer(2);

        // Act
        double result = crrPricer.price(option);

        // Assert
        assertThat(result).isEqualTo(53.39, withPrecision(0.01));
    }

    //----------------------------------------------------------------------
    //endregion
}
