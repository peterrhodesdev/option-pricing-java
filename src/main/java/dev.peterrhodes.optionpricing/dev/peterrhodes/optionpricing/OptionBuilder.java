package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.internal.OptionImpl;
import dev.peterrhodes.optionpricing.internal.utils.ValidationUtils;

/**
 * Builds a customizable {@link Option}.
 */
public final class OptionBuilder {

    private OptionImpl option;

    /**
     * Creates the base object for building a customizable {@link Option}.
     * <ul>The minimum required configuration is:
     *   <li>Select the style of the option:
     *     <ul>
     *       <li>{@link americanStyle}</li>
     *       <li>{@link europeanStyle}</li>
     *     </ul>
     *   </li>
     *   <li>Select the type of the option:
     *     <ul>
     *       <li>{@link asCall}</li>
     *       <li>{@link asPut}</li>
     *     </ul>
     *   </li>
     * </ul>
     *
     * @param initialSpotPrice Initial price of the underlying asset ({@code S₀ > 0}).
     * @param strikePrice Strike/exercise price of the option ({@code K > 0}).
     * @param timeToMaturity Time until maturity/expiration in years ({@code τ = T - t > 0}).
     * @param volatility Underlying volatility ({@code σ > 0}).
     * @param riskFreeRate Annualized risk-free interest rate continuously compounded ({@code r}).
     * @param dividendYield Annual dividend yield continuously compounded ({@code q}).
     * @throws NullPointerException if any of the arguments are null
     * @throws IllegalArgumentException if {@code initialSpotPrice}, {@code strikePrice}, {@code timeToMaturity}, or {@code volatility} are not greater than zero
     */
    public OptionBuilder(
        Number initialSpotPrice,
        Number strikePrice,
        Number timeToMaturity,
        Number volatility,
        Number riskFreeRate,
        Number dividendYield
    ) throws NullPointerException, IllegalArgumentException {
        ValidationUtils.checkNotNull(initialSpotPrice, "initialSpotPrice");
        ValidationUtils.checkNotNull(strikePrice, "strikePrice");
        ValidationUtils.checkNotNull(timeToMaturity, "timeToMaturity");
        ValidationUtils.checkNotNull(volatility, "volatility");
        ValidationUtils.checkNotNull(riskFreeRate, "riskFreeRate");
        ValidationUtils.checkNotNull(dividendYield, "dividendYield");

        ValidationUtils.checkGreaterThanZero(initialSpotPrice, "initialSpotPrice");
        ValidationUtils.checkGreaterThanZero(strikePrice, "strikePrice");
        ValidationUtils.checkGreaterThanZero(timeToMaturity, "timeToMaturity");
        ValidationUtils.checkGreaterThanZero(volatility, "volatility");

        this.option = new OptionImpl(initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    //region option style
    //----------------------------------------------------------------------

    /**
     * Configures the option style as 'American', i.e.&nbsp;it can be exercised at any time up to and including the expiration date..
     */
    public OptionBuilder americanStyle() {
        this.option.setOptionStyle(OptionStyle.AMERICAN);
        return this;
    }

    /**
     * Configures the option style as 'European', i.e.&nbsp;it can only be exercised at maturity (the option's expiration date).
     */
    public OptionBuilder europeanStyle() {
        this.option.setOptionStyle(OptionStyle.EUROPEAN);
        return this;
    }

    //----------------------------------------------------------------------
    //endregion option style

    //region option type
    //----------------------------------------------------------------------

    /**
     * Configures the option type as 'call', i.e.&nbsp;the holder has the right, but not the obligation, to buy an asset.
     */
    public OptionBuilder asCall() {
        this.option.setOptionType(OptionType.CALL);
        return this;
    }

    /**
     * Configures the option type as 'put', i.e.&nbsp;the holder has the right, but not the obligation, to sell an asset.
     */
    public OptionBuilder asPut() {
        this.option.setOptionType(OptionType.PUT);
        return this;
    }

    //----------------------------------------------------------------------
    //endregion option type

    /**
     * Builds the option.
     *
     * @return the option
     * @throws IllegalStateException if the type (call or put) and/or the style (European, American, ...) haven't been configured
     */
    public Option build() throws IllegalStateException {
        if (this.option.optionStyle() == null) {
            throw new IllegalStateException("option style not configured");
        }
        if (this.option.optionType() == null) {
            throw new IllegalStateException("option type not configured");
        }
        return this.option.clone();
    }
}
