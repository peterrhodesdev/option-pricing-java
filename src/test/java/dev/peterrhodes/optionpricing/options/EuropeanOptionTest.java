package dev.peterrhodes.optionpricing.options;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import dev.peterrhodes.optionpricing.core.Calculation;
import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.core.Parameter;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link EuropeanOption}.
 * References:
 * - Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
 * - Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
 */
class EuropeanOptionTest {

    private final String greaterThanZeroMessage = "must be greater than zero";

    //region throws IllegalArgumentException tests
    //----------------------------------------------------------------------

    @Test
    void throwsZeroSpotPrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 0.0, 100.0, 1.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void throwsZeroStrikePrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 0.0, 1.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void throwsZeroTimeToExpiration() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 100.0, 100.0, 0.0, 0.25, 0.1, 0.05);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    @Test
    void throwsZeroVolatility() {
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

    /**
     * Hull (2014): page 360, section 15.9, Example 15.6
     */
    @SuppressWarnings("checkstyle:multiplevariabledeclarations")
    @Test
    void priceHull2014Ex156() {
        // Arrange
        double S = 42, K = 40, T = 0.5, vol = 0.2, r = 0.1, q = 0;
        EuropeanOption call = new EuropeanOption(OptionType.CALL, S, K, T, vol, r, q);
        EuropeanOption put = new EuropeanOption(OptionType.PUT, S, K, T, vol, r, q);

        // Act
        double callResult = call.price();
        double putResult = put.price();

        // Assert
        assertThat(callResult).isEqualTo(4.76, withPrecision(0.01));
        assertThat(putResult).isEqualTo(0.81, withPrecision(0.01));
    }

    /**
     * Hull (2014): page 363, section 15.10, Example 15.7
     */
    @Test
    void priceHull2014Ex157() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 40, 60, 5, 0.3, 0.03, 0);

        // Act
        double result = option.price();

        // Assert
        assertThat(result).isEqualTo(7.04, withPrecision(0.01));
    }

    // TODO: Hull (2014) Chapter 15 Practice Questions
    // TODO: Hull (2014) Chapter 16

    /**
     * Hull (2014): page 396, section 17.4, Example 17.1
     */
    @Test
    void priceHull2014Ex171() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 930, 900, 2 / 12d, 0.2, 0.08, 0.03);

        // Act
        double result = option.price();

        // Assert
        assertThat(result).isEqualTo(51.83, withPrecision(0.01));
    }

    /**
     * Hull (2014): page 399, section 17.5, Example 17.2
     */
    @Test
    void priceHull2014Ex172() {
        // Arrange
        EuropeanOption optionVolatilty10 = new EuropeanOption(OptionType.CALL, 1.6, 1.6, 0.3333, 0.1, 0.08, 0.11);
        EuropeanOption optionVolatilty20 = new EuropeanOption(OptionType.CALL, 1.6, 1.6, 0.3333, 0.2, 0.08, 0.11);

        // Act
        double resultVolatilty10 = optionVolatilty10.price();
        double resultVolatilty20 = optionVolatilty20.price();

        // Assert
        assertThat(resultVolatilty10).isEqualTo(0.0285, withPrecision(0.0001));
        assertThat(resultVolatilty20).isEqualTo(0.0639, withPrecision(0.0001));
    }

    // TODO: Hull (2014) Chapter 17 Practice Questions

    //----------------------------------------------------------------------
    //endregion

    //region delta tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 427, section 19.4, Example 19.1
     */
    @Test
    void deltaHull2014Ex191() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double value = option.delta();

        // Assert
        assertThat(value).isEqualTo(0.522, withPrecision(0.001));

        // Formula
        Formula formula = option.deltaFormula();
        System.out.println("*****\nFORMULA\n*****");
        System.out.println("LHS = " + formula.getLhs());
        System.out.println("RHS = " + formula.getRhs());
        System.out.println("ALT = " + formula.getRhsSimplified());
        System.out.println("where: ");
        for (String whereComponent : formula.getWhereComponents()) {
            System.out.println("    " + whereComponent);
        }
        System.out.println("parameters: ");
        for (Parameter parameter : formula.getParameters()) {
            System.out.println("    notation: " + parameter.getNotation() + ", definition: " + parameter.getDefinition());
        }

        // Calculation
        Calculation calculation = option.deltaCalculation();
        System.out.println("*****\nCALCULATION\n*****");
        System.out.println("inputs: ");
        for (EquationInput input : calculation.getInputs()) {
            System.out.println("    Key : " + input.getKey() + ", Value : " + input.getValue());
        }
        System.out.println("steps: ");
        for (String step : calculation.getSteps()) {
            System.out.println("    " + step);
        }
        System.out.println("answer: " + calculation.getAnswer());
    }

    /**
     * Hull (2014): page 445, section 19.13, Example 19.9
     */
    @Test
    void deltaHull2014Ex199() {
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

    /**
     * Hull (2014): page 436, section 19.6, Example 19.4
     */
    @Test
    void gammaHull2014Ex194() {
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

    /**
     * Hull (2014): page 438, section 19.8, Example 19.6
     */
    @Test
    void gammaHull2014Ex196() {
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

    /**
     * Hull (2014): page 431, section 19.5, Example 19.2
     */
    @Test
    void thetaHull2014Ex192() {
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

    /**
     * Hull (2014): page 439, section 19.9, Example 19.7
     */
    @Test
    void rhoHull2014Ex197() {
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
