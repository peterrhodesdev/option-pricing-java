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

    //region IllegalArgumentException
    //----------------------------------------------------------------------

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

    //----------------------------------------------------------------------
    //endregion

    //region analyticalPrice
    //----------------------------------------------------------------------

    /*
     * Hull: Options, Futures, and Other Derivatives (9th edition)
     * page 360, section 15.9, Example 15.6
     */
    @Test
    void analyticalPrice_Hull_Ex15_6() {
        // Arrange
        double S = 42, K = 40, T = 0.5, v = 0.2, r = 0.1, q = 0;
        EuropeanOption call = new EuropeanOption(OptionType.CALL, S, K, T, v, r, q);
        EuropeanOption put = new EuropeanOption(OptionType.PUT, S, K, T, v, r, q);

        // Act
        double call_result = call.analyticalPrice();
        double put_result = put.analyticalPrice();

        // Assert
        assertThat(call_result).isEqualTo(4.76, withPrecision(0.01));
        assertThat(put_result).isEqualTo(0.81, withPrecision(0.01));
    }

    /*
     * Hull: Options, Futures, and Other Derivatives (9th edition)
     * page 363, section 15.10, Example 15.7
     */
    @Test
    void analyticalPrice_Hull_Ex15_7() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 40, 60, 5, 0.3, 0.03, 0);

        // Act
        double result = option.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(7.04, withPrecision(0.01));
    }

    /*
     * Hull: Options, Futures, and Other Derivatives (9th edition)
     * pages 370-373, Chapter 15 Practice Questions
     */
    @Test
    void analyticalPrice_Hull_Chapter15_PracticeQuestions() {
        // TODO
    }

    // TODO: Hull Chapter 16

    /*
     * Hull: Options, Futures, and Other Derivatives (9th edition)
     * page 396, section 17.4, Example 17.1
     */
    @Test
    void analyticalPrice_Hull_Ex17_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 930, 900, 2d/12d, 0.2, 0.08, 0.03);

        // Act
        double result = option.analyticalPrice();

        // Assert
        assertThat(result).isEqualTo(51.83, withPrecision(0.01));
    }

    /*
     * Hull: Options, Futures, and Other Derivatives (9th edition)
     * page 399, section 17.5, Example 17.2
     */
    @Test
    void analyticalPrice_Hull_Ex17_2() {
        // Arrange
        EuropeanOption option_volatilty10 = new EuropeanOption(OptionType.CALL, 1.6, 1.6, 0.3333, 0.1, 0.08, 0.11);
        EuropeanOption option_volatilty20 = new EuropeanOption(OptionType.CALL, 1.6, 1.6, 0.3333, 0.2, 0.08, 0.11);

        // Act
        double result_volatilty10 = option_volatilty10.analyticalPrice();
        double result_volatilty20 = option_volatilty20.analyticalPrice();

        // Assert
        assertThat(result_volatilty10).isEqualTo(0.0285, withPrecision(0.0001));
        assertThat(result_volatilty20).isEqualTo(0.0639, withPrecision(0.0001));
    }

    /*
     * Hull: Options, Futures, and Other Derivatives (9th edition)
     * pages 402-404, Chapter 17 Practice Questions
     */
    @Test
    void analyticalPrice_Hull_Chapter17_PracticeQuestions() {
        // TODO
    }

    //----------------------------------------------------------------------
    //endregion

    //region analyticalCalculation
    //----------------------------------------------------------------------

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

    @Test
    void analyticalCalculation_put_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.PUT, 100.0, 100.0, 1.0, 0.25, 0.1, 0.05);

        // Act
        AnalyticalCalculation result = option.analyticalCalculation();

        // Assert
        assertThat(result.getPrice()).as("price").isEqualTo(7.10, withPrecision(this.pricePrecision));
        assertThat(result.getDelta()).as("delta").isEqualTo(-0.354, withPrecision(this.greekPrecision));
        assertThat(result.getGamma()).as("gamma").isEqualTo(0.014, withPrecision(this.greekPrecision));
        assertThat(result.getVega()).as("vega").isEqualTo(35.996, withPrecision(this.greekPrecision));
        assertThat(result.getTheta()).as("theta").isEqualTo(-2.018, withPrecision(this.greekPrecision));
        assertThat(result.getRho()).as("rho").isEqualTo(-42.537, withPrecision(this.greekPrecision));
    }

    //----------------------------------------------------------------------
    //endregion
}
