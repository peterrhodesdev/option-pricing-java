package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;
//import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

class EuropeanOptionTest {

    /*
     * References:
     * - Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
     * - Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
     */

    private final String greaterThanZeroMessage = "must be greater than zero";
    private final double pricePrecision = 0.01;
    private final double greekPrecision = 0.001;

    //region throws IllegalArgumentException tests
    //----------------------------------------------------------------------

    @Test
    void throws_zeroSpotPrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 0.0, 100.0, 1.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void throws_zeroStrikePrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 0.0, 1.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void throws_zeroTimeToExpiration() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 0.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void throws_zeroVolatility() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 1.0, 0.0, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    //----------------------------------------------------------------------
    //endregion

    //region price tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 360, section 15.9, Example 15.6
     */
    @Test
    void price_Hull2014_Ex15_6() {
        // Arrange
        double S = 42, K = 40, T = 0.5, v = 0.2, r = 0.1, q = 0;
        EuropeanOption call = new EuropeanOption(OptionType.CALL, S, K, T, v, r, q);
        EuropeanOption put = new EuropeanOption(OptionType.PUT, S, K, T, v, r, q);

        // Act
        double call_result = call.price();
        double put_result = put.price();

        // Assert
        assertThat(call_result).isEqualTo(4.76, withPrecision(0.01));
        assertThat(put_result).isEqualTo(0.81, withPrecision(0.01));
    }

    /*
     * Hull (2014): page 363, section 15.10, Example 15.7
     */
    @Test
    void price_Hull2014_Ex15_7() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 40, 60, 5, 0.3, 0.03, 0);

        // Act
        double result = option.price();

        // Assert
        assertThat(result).isEqualTo(7.04, withPrecision(0.01));
    }

    // TODO: Hull (2014) Chapter 15 Practice Questions
    // TODO: Hull (2014) Chapter 16

    /*
     * Hull (2014): page 396, section 17.4, Example 17.1
     */
    @Test
    void price_Hull2014_Ex17_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 930, 900, 2d/12d, 0.2, 0.08, 0.03);

        // Act
        double result = option.price();

        // Assert
        assertThat(result).isEqualTo(51.83, withPrecision(0.01));
    }

    /*
     * Hull (2014): page 399, section 17.5, Example 17.2
     */
    @Test
    void price_Hull2014_Ex17_2() {
        // Arrange
        EuropeanOption option_volatilty10 = new EuropeanOption(OptionType.CALL, 1.6, 1.6, 0.3333, 0.1, 0.08, 0.11);
        EuropeanOption option_volatilty20 = new EuropeanOption(OptionType.CALL, 1.6, 1.6, 0.3333, 0.2, 0.08, 0.11);

        // Act
        double result_volatilty10 = option_volatilty10.price();
        double result_volatilty20 = option_volatilty20.price();

        // Assert
        assertThat(result_volatilty10).isEqualTo(0.0285, withPrecision(0.0001));
        assertThat(result_volatilty20).isEqualTo(0.0639, withPrecision(0.0001));
    }

    // TODO: Hull (2014) Chapter 17 Practice Questions

    //----------------------------------------------------------------------
    //endregion

    //region delta tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 427, section 19.4, Example 19.1
     */
    @Test
    void delta_Hull2014_Ex19_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.delta();

        // Assert
        assertThat(result).isEqualTo(0.522, withPrecision(0.001));
    }

    /*
     * Hull (2014): page 445, section 19.13, Example 19.9
     */
    @Test
    void delta_Hull2014_Ex19_9() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.PUT, 90, 87, 0.5, 0.25, 0.09, 0.03);

        // Act
        double result = option.delta();

        // Assert
        assertThat(result).isEqualTo(-0.3215, withPrecision(0.0001));
    }

    //----------------------------------------------------------------------
    //endregion

    //region gamma tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 436, section 19.6, Example 19.4
     */
    @Test
    void gamma_Hull2014_Ex19_4() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.gamma();

        // Assert
        assertThat(result).isEqualTo(0.066, withPrecision(0.001));
    }

    //----------------------------------------------------------------------
    //endregion

    //region vega tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 438, section 19.8, Example 19.6
     */
    @Test
    void gamma_Hull2014_Ex19_6() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.vega();

        // Assert
        assertThat(result).isEqualTo(12.1, withPrecision(0.1));
    }

    //----------------------------------------------------------------------
    //endregion

    //region theta tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 431, section 19.5, Example 19.2
     */
    @Test
    void theta_Hull2014_Ex19_2() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.theta();

        // Assert
        assertThat(result).isEqualTo(-4.31, withPrecision(0.01));
    }

    // TODO put

    //----------------------------------------------------------------------
    //endregion

    //region rho tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 439, section 19.9, Example 19.7
     */
    @Test
    void rho_Hull2014_Ex19_7() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.rho();

        // Assert
        assertThat(result).isEqualTo(8.91, withPrecision(0.01));
    }

    // TODO put

    //----------------------------------------------------------------------
    //endregion

    // TODO Hull (2014) Chapter 19 Practice Questions
}
