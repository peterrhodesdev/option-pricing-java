package dev.peterrhodes.optionpricing.options;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import dev.peterrhodes.optionpricing.models.CalculationModel;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #EuropeanOption}.
 * References:
 * <ul>
 *   <li>Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.</li>
 *   <li>Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.</li>
 * </ul>
 */
@SuppressWarnings("checkstyle:multiplevariabledeclarations")
class EuropeanOptionTest {

    private void assertCalculation(CalculationModel result, int[] expectedStepLengths, String[][] expectedStepContains, String[] expectedStepAnswers, double expectedAnswer, double answerPrecision) {
        int expectedStepsLength = expectedStepLengths.length;

        String[][] steps = result.getSteps();

        /*for (String[] step : steps) {
            System.out.println("step");
            for (String part : step) {
                System.out.println(part);
            }
        }*/

        assertThat(steps.length)
            .as("number of steps")
            .isEqualTo(expectedStepLengths.length);

        for (int i = 0; i < expectedStepLengths.length; i++) {
            // number of step parts
            assertThat(steps[i].length)
                .as(String.format("number of parts in step %d", i))
                .isEqualTo(expectedStepLengths[i]);

            // values substituted into equation
            int substitutionPartIndex = steps[i].length - 2;
            String substitutionPart = steps[i][substitutionPartIndex];
            for (String chars : expectedStepContains[i]) {
                assertThat(substitutionPart.contains(chars))
                    .as(String.format("step %d, part %d '%s', contains '%s'", i, substitutionPartIndex, substitutionPart, chars))
                    .isTrue();
            }

            // answer
            assertThat(expectedStepAnswers[i])
                .as(String.format("step %d last element (answer)", i))
                .isEqualTo(steps[i][expectedStepLengths[i] - 1]);
        }

        assertThat(result.getAnswer()).isEqualTo(expectedAnswer, withPrecision(answerPrecision));
    }

    //region throws tests
    //----------------------------------------------------------------------

    @Test
    void Invalid_argument_values_should_throw_IllegalArgumentException() {
        // Arrange
        double S = 100.0, K = 100.0, τ = 1.0, σ = 0.25, r = 0.1, q = 0.05;
        Class exClass = IllegalArgumentException.class;
        String greaterThanZeroMessage = "must be greater than zero";

        // Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = EuropeanOption.createCall(0, K, τ, σ, r, q);
        })
            .as("zero spot price")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);

        assertThatThrownBy(() -> {
            EuropeanOption ex = EuropeanOption.createCall(S, 0, τ, σ, r, q);
        })
            .as("zero strike price")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);

        assertThatThrownBy(() -> {
            EuropeanOption ex = EuropeanOption.createCall(S, K, 0, σ, r, q);
        })
            .as("zero time to maturity")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);

        assertThatThrownBy(() -> {
            EuropeanOption ex = EuropeanOption.createCall(S, K, τ, 0, r, q);
        })
            .as("zero volatiliy")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);
    }

    //----------------------------------------------------------------------
    //endregion

    //region price tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 360, section 15.9, Example 15.6.
     */
    @Test
    void Prices_for_call_and_put_with_same_parameters_and_no_dividend_Hull2014Ex156() {
        // Arrange
        double S = 42, K = 40, τ = 0.5, σ = 0.2, r = 0.1, q = 0;
        EuropeanOption call = EuropeanOption.createCall(S, K, τ, σ, r, q);
        EuropeanOption put = EuropeanOption.createPut(S, K, τ, σ, r, q);

        // Act
        double callResult = call.price();
        double putResult = put.price();

        // Assert
        assertThat(callResult).isEqualTo(4.76, withPrecision(0.01));
        assertThat(putResult).isEqualTo(0.81, withPrecision(0.01));
    }

    /**
     * Hull (2014): page 363, section 15.10, Example 15.7.
     */
    @Test
    void Price_for_call_with_no_dividend_Hull2014Ex157() {
        // Arrange
        EuropeanOption option = EuropeanOption.createCall(40, 60, 5, 0.3, 0.03, 0);

        // Act
        double result = option.price();

        // Assert
        assertThat(result).isEqualTo(7.04, withPrecision(0.01));
    }

    /**
     * Hull (2014): page 396, section 17.4, Example 17.1.
     */
    @Test
    void Price_for_call_with_dividend_Hull2014Ex171() {
        // Arrange
        EuropeanOption option = EuropeanOption.createCall(930, 900, 2 / 12d, 0.2, 0.08, 0.03);

        // Act
        double result = option.price();

        // Assert
        assertThat(result).isEqualTo(51.83, withPrecision(0.01));
    }

    // TODO put with dividend

    //----------------------------------------------------------------------
    //endregion

    //region delta tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 427, section 19.4, Example 19.1.
     */
    @Test
    void Delta_for_call_with_no_dividend_Hull2014Ex191() {
        // Arrange
        EuropeanOption option = EuropeanOption.createCall(49, 50, 0.3846, 0.2, 0.05, 0);
        option.setCalculationStepPrecision(3, RoundingMethod.SIGNIFICANT_FIGURES);

        // Act
        CalculationModel result = option.deltaCalculation();
        double value = option.delta();

        // Assert
        // steps: d₁, N(d₁), Δ
        int[] expectedStepLengths = { 4, 3, 5 };
        String[][] expectedStepContains = {
            // S       K       T           σ        r         q
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            // d₁
            { " 0.0542 " },
            // T           q      d₁
            { " 0.3846 ", " 0 ", " 0.0542 " }
        };
        String[] expectedStepAnswers = { "0.0542", "0.522", "0.522" };

        this.assertCalculation(result, expectedStepLengths, expectedStepContains, expectedStepAnswers, 0.522, 0.001);
        assertThat(result.getAnswer()).as("model value same as double method").isEqualTo(value);
    }

    // TODO call dividend, put no dividend

    /**
     * Hull (2014): page 445, section 19.13, Example 19.9.
     */
    @Test
    void Delta_for_put_Hull2014Ex199() {
        // Arrange
        EuropeanOption option = EuropeanOption.createPut(90, 87, 0.5, 0.25, 0.09, 0.03);

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
     * Hull (2014): page 436, section 19.6, Example 19.4.
     */
    @Test
    void Gamma_for_option_with_no_dividend_Hull2014Ex194() {
        // Arrange
        double S = 49, K = 50, τ = 0.3846, σ = 0.2, r = 0.05, q = 0;
        EuropeanOption call = EuropeanOption.createCall(S, K, τ, σ, r, q);
        EuropeanOption put = EuropeanOption.createPut(S, K, τ, σ, r, q);

        // Act
        double callResult = call.gamma();
        double putResult = put.gamma();

        // Assert
        assertThat(callResult).as("call answer").isEqualTo(0.066, withPrecision(0.001));
        assertThat(callResult).as("call = put").isEqualTo(putResult);
    }

    // TODO dividend

    //----------------------------------------------------------------------
    //endregion

    //region vega tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 438, section 19.8, Example 19.6.
     */
    @Test
    void Vega_for_option_with_no_dividend_Hull2014Ex196() {
        // Arrange
        double S = 49, K = 50, τ = 0.3846, σ = 0.2, r = 0.05, q = 0;
        EuropeanOption call = EuropeanOption.createCall(S, K, τ, σ, r, q);
        EuropeanOption put = EuropeanOption.createPut(S, K, τ, σ, r, q);

        // Act
        double callResult = call.vega();
        double putResult = put.vega();

        // Assert
        assertThat(callResult).as("call answer").isEqualTo(12.1, withPrecision(0.1));
        assertThat(callResult).as("call = put").isEqualTo(putResult);
    }

    // TODO dividend

    //----------------------------------------------------------------------
    //endregion

    //region theta tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 431, section 19.5, Example 19.2.
     */
    @Test
    void Theta_for_call_with_no_dividend_Hull2014Ex192() {
        // Arrange
        EuropeanOption option = EuropeanOption.createCall(49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.theta();

        // Assert
        assertThat(result).isEqualTo(-4.31, withPrecision(0.01));
    }

    // TODO dividend, put

    //----------------------------------------------------------------------
    //endregion

    //region rho tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 439, section 19.9, Example 19.7.
     */
    @Test
    void Rho_for_call_with_no_dividend_Hull2014Ex197() {
        // Arrange
        EuropeanOption option = EuropeanOption.createCall(49, 50, 0.3846, 0.2, 0.05, 0);

        // Act
        double result = option.rho();

        // Assert
        assertThat(result).isEqualTo(8.91, withPrecision(0.01));
    }

    // TODO dividend, put

    //----------------------------------------------------------------------
    //endregion
}
