package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import lombok.NonNull;

/**
 * Builds a customizable {@link Option}.
 */
public final class OptionBuilder {

    private ContractImpl contract;

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
        @NonNull Number initialSpotPrice,
        @NonNull Number strikePrice,
        @NonNull Number timeToMaturity,
        @NonNull Number volatility,
        @NonNull Number riskFreeRate,
        @NonNull Number dividendYield
    ) throws NullPointerException, IllegalArgumentException {
        this.checkGreaterThanZero(initialSpotPrice, "initialSpotPrice");
        this.checkGreaterThanZero(strikePrice, "strikePrice");
        this.checkGreaterThanZero(timeToMaturity, "timeToMaturity");
        this.checkGreaterThanZero(volatility, "volatility");

        this.contract = new ContractImpl(initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    //region option style
    //----------------------------------------------------------------------

    /**
     * Configures the option style as 'American', i.e.&nbsp;it can be exercised at any time up to and including the expiration date..
     */
    public OptionBuilder americanStyle() {
        this.contract.setOptionStyle(OptionStyle.AMERICAN);
        return this;
    }

    /**
     * Configures the option style as 'European', i.e.&nbsp;it can only be exercised at maturity (the option's expiration date).
     */
    public OptionBuilder europeanStyle() {
        this.contract.setOptionStyle(OptionStyle.EUROPEAN);
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
        this.contract.setOptionType(OptionType.CALL);
        return this;
    }

    /**
     * Configures the option type as 'put', i.e.&nbsp;the holder has the right, but not the obligation, to sell an asset.
     */
    public OptionBuilder asPut() {
        this.contract.setOptionType(OptionType.PUT);
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
        if (this.contract.getOptionStyle() == null) {
            throw new IllegalStateException("option style not configured");
        }
        if (this.contract.getOptionType() == null) {
            throw new IllegalStateException("option type not configured");
        }
        return new OptionImpl(this.contract);
    }

    //region private methods
    //----------------------------------------------------------------------

    private static void checkGreaterThanZero(Number value, String name) throws IllegalArgumentException {
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
    }
}
