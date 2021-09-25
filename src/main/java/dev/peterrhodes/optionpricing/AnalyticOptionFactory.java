package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.analyticoptions.EuropeanOption;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import lombok.NonNull;

/**
 * Factory for options where the price has been analytically determined, see {@link AnalyticOption}.
 */
public interface AnalyticOptionFactory {

    //region European
    //----------------------------------------------------------------------

    /**
     * Creates a vanilla European call option.
     *
     * @param initialSpotPrice Initial price of the underlying asset ({@code S₀ > 0}).
     * @param strikePrice Strike/exercise price of the option ({@code K > 0}).
     * @param timeToMaturity Time until maturity/expiration in years ({@code τ = T - t > 0}).
     * @param volatility Underlying volatility ({@code σ > 0}).
     * @param riskFreeRate Annualized risk-free interest rate continuously compounded ({@code r}).
     * @param dividendYield Annual dividend yield continuously compounded ({@code q}).
     * @return European call option
     * @throws NullPointerException if any of the arguments are null
     * @throws IllegalArgumentException if {@code initialSpotPrice}, {@code strikePrice}, {@code timeToMaturity}, or {@code volatility} are not greater than zero
     */
    static AnalyticOption createEuropeanCall(
        @NonNull Number initialSpotPrice,
        @NonNull Number strikePrice,
        @NonNull Number timeToMaturity,
        @NonNull Number volatility,
        @NonNull Number riskFreeRate,
        @NonNull Number dividendYield
    ) throws NullPointerException, IllegalArgumentException {
        return createEuropean(OptionType.CALL, initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    /**
     * Creates a vanilla European put option.
     *
     * @param initialSpotPrice Initial price of the underlying asset ({@code S₀ > 0}).
     * @param strikePrice Strike/exercise price of the option ({@code K > 0}).
     * @param timeToMaturity Time until maturity/expiration in years ({@code τ = T - t > 0}).
     * @param volatility Underlying volatility ({@code σ > 0}).
     * @param riskFreeRate Annualized risk-free interest rate continuously compounded ({@code r}).
     * @param dividendYield Annual dividend yield continuously compounded ({@code q}).
     * @return European put option
     * @throws NullPointerException if any of the arguments are null
     * @throws IllegalArgumentException if {@code initialSpotPrice}, {@code strikePrice}, {@code timeToMaturity}, or {@code volatility} are not greater than zero
     */
    static AnalyticOption createEuropeanPut(
        @NonNull Number initialSpotPrice,
        @NonNull Number strikePrice,
        @NonNull Number timeToMaturity,
        @NonNull Number volatility,
        @NonNull Number riskFreeRate,
        @NonNull Number dividendYield
    ) throws NullPointerException, IllegalArgumentException {
        return createEuropean(OptionType.PUT, initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    private static AnalyticOption createEuropean(OptionType optionType, Number initialSpotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) {
        checkGreaterThanZero(initialSpotPrice, "initialSpotPrice");
        checkGreaterThanZero(strikePrice, "strikePrice");
        checkGreaterThanZero(timeToMaturity, "timeToMaturity");
        checkGreaterThanZero(volatility, "volatility");

        ContractImpl contract = new ContractImpl(initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
        contract.setOptionStyle(OptionStyle.EUROPEAN);
        contract.setOptionType(optionType);

        return new EuropeanOption(contract);
    }

    //----------------------------------------------------------------------
    //endregion European

    //region private methods
    //----------------------------------------------------------------------

    private static void checkGreaterThanZero(Number value, String name) throws IllegalArgumentException {
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
    }
}
