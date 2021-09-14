package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;

/**
 * Interface for an option that doesn't have an analytical solution.
 * If the specific option has an analytical solution then it will extend {@link AnalyticalOption}.
 */
public interface Option {

    /**
     * Gets the style of the option (e.g. European, American, ...).
     *
     * @return option style
     */
    OptionStyle getStyle();

    /**
     * Gets the type of the option (call or put).
     *
     * @return option type
     */
    OptionType getType();

    /**
     * Gets the price of the underlying asset.
     *
     * @return spot price (S)
     */
    double getS();

    /**
     * Gets the strike price of the option.
     *
     * @return strike/exercise price (K)
     */
    double getK();

    /**
     * Gets the time from the start of the option contract until maturity.
     *
     * @return time until option expiration (T)
     */
    double getT();

    /**
     * Gets the volatility of the option.
     *
     * @return volatility (σ)
     */
    double getVol();

    /**
     * Gets the annualized risk-free interest rate.
     *
     * @return risk-free rate (r)
     */
    double getR();

    /**
     * Gets the continuous dividend yield.
     *
     * @return dividend yield (q)
     */
    double getQ();
}
