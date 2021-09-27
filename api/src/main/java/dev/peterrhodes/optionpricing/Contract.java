package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.internal.common.ExerciseValueInput;

/**
 * Defines the option contract.
 */
public interface Contract {

    /**
     * Calculates the value of exercising the option (assuming all preconditions are met).
     *
     * @param exerciseValueInput inputs required to calculate the exercise value
     * @return exercise value
     */
    default double exerciseValue(ExerciseValueInput exerciseValueInput) {
        double τ = this.timeToMaturity().doubleValue();
        double C̟P̠ = this.optionType() == OptionType.CALL ? 1d : -1d;

        double t = exerciseValueInput.getTime();
        double S_t = exerciseValueInput.getSpotPrice();
        double K = this.strikePrice().doubleValue();

        switch (this.optionStyle()) {
            case EUROPEAN:
                return t < τ ? 0d : Math.max(0d, C̟P̠ * (S_t - K));
            case AMERICAN:
                return Math.max(0d, C̟P̠ * (S_t - K));
            default:
                throw new IllegalStateException(this.optionStyle().toString() + " enum value not handled");
        }
    }

    /**
     * Style of the option defined by the exercise rights, e.g.&nbsp;European, American.
     */
    OptionStyle optionStyle();

    /**
     * Type of the option, i.e.&nbsp;call or put.
     */
    OptionType optionType();

    /**
     * Returns the initial spot price.
     *
     * @return Initial price of the underlying asset ({@code S₀ > 0}).
     */
    Number initialSpotPrice();

    /**
     * Returns the strike price.
     *
     * @return Strike/exercise price of the option ({@code K > 0}).
     */
    Number strikePrice();

    /**
     * Returns the time to maturity.
     *
     * @return Time until maturity/expiration in years ({@code τ = T - t > 0}).
     */
    Number timeToMaturity();

    /**
     * Returns the volatility.
     *
     * @return Underlying volatility ({@code σ > 0}).
     */
    Number volatility();

    /**
     * Returns the risk-free rate.
     *
     * @return Annualized risk-free interest rate continuously compounded ({@code r}).
     */
    Number riskFreeRate();

    /**
     * Returns the dividend yield.
     *
     * @return Annual dividend yield continuously compounded ({@code q}).
     */
    Number dividendYield();
}
