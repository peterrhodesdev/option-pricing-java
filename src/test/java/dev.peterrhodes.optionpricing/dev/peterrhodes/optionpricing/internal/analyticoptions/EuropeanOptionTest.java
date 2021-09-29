package dev.peterrhodes.optionpricing.internal.analyticoptions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import dev.peterrhodes.optionpricing.AnalyticOption;
import dev.peterrhodes.optionpricing.AnalyticOptionFactory;
import dev.peterrhodes.optionpricing.internal.enums.PrecisionType;
import dev.peterrhodes.optionpricing.models.AnalyticCalculation;
import org.junit.jupiter.api.Disabled;
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
public class EuropeanOptionTest {

    @Test
    public void Invalid_argument_values_should_throw_IllegalArgumentException() {
        // Arrange
        double S = 100.0, K = 100.0, τ = 1.0, σ = 0.25, r = 0.1, q = 0.05;
        Class exClass = IllegalArgumentException.class;
        String greaterThanZeroMessage = "must be greater than zero";

        // Act Assert
        assertThatThrownBy(() -> {
            AnalyticOption ex = AnalyticOptionFactory.createEuropeanCall(0, K, τ, σ, r, q);
        })
            .as("zero spot price")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);

        assertThatThrownBy(() -> {
            AnalyticOption ex = AnalyticOptionFactory.createEuropeanCall(S, 0, τ, σ, r, q);
        })
            .as("zero strike price")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);

        assertThatThrownBy(() -> {
            AnalyticOption ex = AnalyticOptionFactory.createEuropeanCall(S, K, 0, σ, r, q);
        })
            .as("zero time to maturity")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);

        assertThatThrownBy(() -> {
            AnalyticOption ex = AnalyticOptionFactory.createEuropeanCall(S, K, τ, 0, r, q);
        })
            .as("zero volatiliy")
            .isInstanceOf(exClass)
            .hasMessageContaining(greaterThanZeroMessage);
    }

    //region price tests
    //----------------------------------------------------------------------

    /**
     * Hull SSM (2014): page 166, Problem 15.13.
     */
    @Test
    public void Price_for_call_with_no_dividend_HullSsm2014P1513() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanCall(52, 50, 0.25, 0.3, 0.12, 0);
        option.setCalculationStepPrecision(4, PrecisionType.DECIMAL_PLACES);

        // Act
        double price = option.price();
        assertThat(price).as("price").isEqualTo(5.06, withPrecision(0.01));

        AnalyticCalculation result = option.priceCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 4, 3, 3, 4 }; // d₁, d₂, N(d₁), N(d₂), price
        String[][] expectedStepSubstitutionContains = {
            { " 52 ", " 50 ", " 0.25 ", " 0.3 ", " 0.12 ", " 0 " },
            { " 52 ", " 50 ", " 0.25 ", " 0.3 ", " 0.12 ", " 0 " },
            { " 0.5365 " }, // d₁
            { " 0.3865 " }, // d₂
            { " 52 ", " 50 ", " 0.25 ", " 0.12 ", " 0 ", " 0.5365 ", " 0.3865" }, // S, K, τ, r, q, d₁, d₂
        };
        String[] expectedStepAnswers = { "0.5365", "0.3865", "0.7042", "0.6504", null }; // price not given to 4.d.p.

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    /**
     * Hull (2014): page 360, section 15.9, Example 15.6.
     */
    @Test
    public void Price_for_put_with_no_dividend_Hull2014Ex156() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanPut(42, 40, 0.5, 0.2, 0.1, 0);
        option.setCalculationStepPrecision(4, PrecisionType.DECIMAL_PLACES);

        // Act
        double price = option.price();
        assertThat(price).as("price").isEqualTo(0.81, withPrecision(0.01));

        AnalyticCalculation result = option.priceCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 4, 3, 3, 4 }; // d₁, d₂, N(-d₁), N(-d₂), price
        String[][] expectedStepSubstitutionContains = {
            { " 42 ", " 40 ", " 0.5 ", " 0.2 ", " 0.1 ", " 0 " },
            { " 42 ", " 40 ", " 0.5 ", " 0.2 ", " 0.1 ", " 0 " },
            { " -0.7693 " }, // -d₁
            { " -0.6278 " }, // -d₂
            { " 42 ", " 40 ", " 0.5 ", " 0.1 ", " 0 ", " - 0.7693 ", " - 0.6278" } // S, K, τ, r, q, -d₁, -d₂
        };
        String[] expectedStepAnswers = { "0.7693", "0.6278", "0.2209", "0.2651", null }; // price not given to 4.d.p.

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    // TODO put with dividend

    //----------------------------------------------------------------------
    //endregion price tests

    //region delta tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 427, section 19.4, Example 19.1.
     */
    @Test
    public void Delta_for_call_with_no_dividend_Hull2014Ex191() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanCall(49, 50, 0.3846, 0.2, 0.05, 0);
        option.setCalculationStepPrecision(3, PrecisionType.SIGNIFICANT_FIGURES);

        // Act
        double greek = option.delta();
        assertThat(greek).as("greek").isEqualTo(0.522, withPrecision(0.001));

        AnalyticCalculation result = option.deltaCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 3, 5 }; // d₁, N(d₁), Δ
        String[][] expectedStepSubstitutionContains = {
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            { " 0.0542 " }, // d₁
            { " 0.3846 ", " 0 ", " 0.0542 " } // τ, q, d₁
        };
        String[] expectedStepAnswers = { "0.0542", "0.522", "0.522" };

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    // TODO call dividend, put no dividend

    /**
     * Hull (2014): page 445, section 19.13, Example 19.9.
     */
    @Test
    public void Delta_for_put_with_dividend_Hull2014Ex199() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanPut(90, 87, 0.5, 0.25, 0.09, 0.03);
        option.setCalculationStepPrecision(4, PrecisionType.DECIMAL_PLACES);

        // Act
        double greek = option.delta();
        assertThat(greek).as("greek").isEqualTo(-0.3215, withPrecision(0.0001));

        AnalyticCalculation result = option.deltaCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 3, 5 }; // d₁, N(-d₁), Δ
        String[][] expectedStepSubstitutionContains = {
            { " 90 ", " 87 ", " 0.5 ", " 0.25 ", " 0.09 ", " 0.03 " },
            { " -0.4499 " }, // -d₁
            { " 0.5 ", " 0.03 ", " - 0.4499 " } // τ, q, -d₁
        };
        String[] expectedStepAnswers = { "0.4499", null, "-0.3215" };

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    //----------------------------------------------------------------------
    //endregion delta tests

    //region gamma tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 436, section 19.6, Example 19.4.
     */
    @Test
    public void Gamma_for_option_with_no_dividend_Hull2014Ex194() {
        // Arrange
        Number S = 49, K = 50, τ = 0.3846, σ = 0.2, r = 0.05, q = 0;
        AnalyticOption call = AnalyticOptionFactory.createEuropeanCall(S, K, τ, σ, r, q);
        call.setCalculationStepPrecision(3, PrecisionType.DECIMAL_PLACES);
        AnalyticOption put = AnalyticOptionFactory.createEuropeanPut(S, K, τ, σ, r, q);
        put.setCalculationStepPrecision(3, PrecisionType.DECIMAL_PLACES);

        // Act
        double callGreek = call.gamma();
        assertThat(callGreek).as("call greek").isEqualTo(0.066, withPrecision(0.001));
        double putGreek = put.gamma();
        assertThat(putGreek).as("put greek").isEqualTo(0.066, withPrecision(0.001));

        AnalyticCalculation callResult = call.gammaCalculation();
        AnalyticCalculation putResult = put.gammaCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 3, 5 }; // d₁, N̕(d₁), Δ
        String[][] expectedStepSubstitutionContains = {
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            {}, // d₁ not given
            { " 49 ", " 0.3846 ", " 0.2 ", " 0 " } // S, τ, σ, q
        };
        String[] expectedStepAnswers = { null, null, "0.066" };

        this.assertCalculation(callResult, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
        this.assertCalculation(putResult, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    // TODO dividend

    //----------------------------------------------------------------------
    //endregion gamma tests

    //region vega tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 438, section 19.8, Example 19.6.
     */
    @Test
    public void Vega_for_option_with_no_dividend_Hull2014Ex196() {
        // Arrange
        Number S = 49, K = 50, τ = 0.3846, σ = 0.2, r = 0.05, q = 0;
        AnalyticOption call = AnalyticOptionFactory.createEuropeanCall(S, K, τ, σ, r, q);
        call.setCalculationStepPrecision(3, PrecisionType.SIGNIFICANT_FIGURES);
        AnalyticOption put = AnalyticOptionFactory.createEuropeanPut(S, K, τ, σ, r, q);
        put.setCalculationStepPrecision(3, PrecisionType.SIGNIFICANT_FIGURES);

        // Act
        double callGreek = call.vega();
        assertThat(callGreek).as("call greek").isEqualTo(12.1, withPrecision(0.1));
        double putGreek = put.vega();
        assertThat(putGreek).as("put greek").isEqualTo(12.1, withPrecision(0.1));

        AnalyticCalculation callResult = call.vegaCalculation();
        AnalyticCalculation putResult = put.vegaCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 3, 5 }; // d₁, N̕(d₁), Δ
        String[][] expectedStepSubstitutionContains = {
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            {}, // d₁ not given
            { " 49 ", " 0.3846 ", " 0 " } // S, τ, q
        };
        String[] expectedStepAnswers = { null, null, "12.1" };

        this.assertCalculation(callResult, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
        this.assertCalculation(putResult, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    // TODO dividend

    //----------------------------------------------------------------------
    //endregion vega tests

    //region theta tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 431, section 19.5, Example 19.2.
     */
    @Test
    public void Theta_for_call_with_no_dividend_Hull2014Ex192() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanCall(49, 50, 0.3846, 0.2, 0.05, 0);
        option.setCalculationStepPrecision(3, PrecisionType.SIGNIFICANT_FIGURES);

        // Act
        double greek = option.theta();
        assertThat(greek).as("greek").isEqualTo(-4.31, withPrecision(0.01));

        AnalyticCalculation result = option.thetaCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 4, 3, 3, 3, 5 }; // d₁, d₂, N(-d₁), N(-d₂), N̕(d₁), ϴ
        String[][] expectedStepSubstitutionContains = {
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            { }, // d values not given
            { },
            { },
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
        };
        String[] expectedStepAnswers = { null, null, null, null, null, "-4.31" };

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    // TODO call dividend, put no/with dividend

    //----------------------------------------------------------------------
    //endregion theta tests

    //region rho tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 439, section 19.9, Example 19.7.
     */
    @Test
    public void Rho_for_call_with_no_dividend_Hull2014Ex197() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanCall(49, 50, 0.3846, 0.2, 0.05, 0);
        option.setCalculationStepPrecision(3, PrecisionType.SIGNIFICANT_FIGURES);

        // Act
        double greek = option.rho();
        assertThat(greek).as("greek").isEqualTo(8.91, withPrecision(0.01));

        AnalyticCalculation result = option.rhoCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 3, 5 }; // d₂, N(d₂), vega
        String[][] expectedStepSubstitutionContains = {
            { " 49 ", " 50 ", " 0.3846 ", " 0.2 ", " 0.05 ", " 0 " },
            {}, // d₂ not given
            { " 50 ", " 0.3846 ", " 0.05 " } // K, τ, r
        };
        String[] expectedStepAnswers = { null, null, "8.91" };

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    // TODO call dividend, put no/with dividend

    //----------------------------------------------------------------------
    //endregion rho tests

    //region disabled tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 396, section 17.4, Example 17.1.
     */
    @Disabled("Likely rounding issue due to the time being given as a fraction. Confirm the results with another source. d₁: book = 0.5444 calc = 0.5445, N(d₁): book = 0.6782 calc = 0.6783")
    @Test
    public void Price_for_call_with_dividend_Hull2014Ex171() {
        // Arrange
        AnalyticOption option = AnalyticOptionFactory.createEuropeanCall(930, 900, 2 / 12d, 0.2, 0.08, 0.03);
        option.setCalculationStepPrecision(4, PrecisionType.SIGNIFICANT_FIGURES);

        // Act
        double price = option.price();
        assertThat(price).as("price").isEqualTo(51.83, withPrecision(0.01));

        AnalyticCalculation result = option.priceCalculation();

        // Assert
        int[] expectedStepLengths = { 4, 4, 3, 3, 4 }; // d₁, d₂, N(d₁), N(d₂), price
        String[][] expectedStepSubstitutionContains = {
            { " 930 ", " 900 ", " 0.2 ", " 0.08 ", " 0.03 " }, // no τ because it's a fraction
            { " 930 ", " 900 ", " 0.2 ", " 0.08 ", " 0.03 " },
            { " 0.5444 " }, // d₁
            { " 0.4628 " }, // d₂
            { " 930 ", " 900 ", " 0.08 ", " 0.03 ", " 0.5444 ", " 0.4628" }, // S, K, r, q, d₁, d₂
        };
        String[] expectedStepAnswers = { "0.5444", "0.4628", "0.7069", "0.6782", "51.83" };

        this.assertCalculation(result, expectedStepLengths, expectedStepSubstitutionContains, expectedStepAnswers);
    }

    //----------------------------------------------------------------------
    //endregion disabled tests

    //region private methods
    //----------------------------------------------------------------------

    private void assertCalculation(AnalyticCalculation result, int[] expectedStepLengths, String[][] expectedStepSubstitutionContains, String[] expectedStepAnswers) {
        int expectedStepsLength = expectedStepLengths.length;

        String[][] steps = result.getSteps();

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
            for (String chars : expectedStepSubstitutionContains[i]) {
                assertThat(substitutionPart.contains(chars))
                    .as(String.format("step %d, part %d '%s', contains '%s'", i, substitutionPartIndex, substitutionPart, chars))
                    .isTrue();
            }

            // answer (null if not given in example)
            if (expectedStepAnswers[i] != null) {
                assertThat(steps[i][expectedStepLengths[i] - 1])
                    .as(String.format("step %d last element (answer)", i))
                    .isEqualTo(expectedStepAnswers[i]);
            }
        }
    }
}
