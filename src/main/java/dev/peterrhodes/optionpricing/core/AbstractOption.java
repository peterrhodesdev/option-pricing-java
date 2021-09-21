package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;

/**
 * Base class for all concrete option classes that don't have an analytical solution.&nbsp;If the specific option has an analytical solution then it should extend {@link AbstractAnalyticalOption}.
 */
public abstract class AbstractOption implements Option {

    /**
     * Style of the option, usually defined by the exercise rights, e.g.&nbsp;European, American.
     */
    @Getter protected OptionStyle style;

    /**
     * Type of the option (call or put).
     */
    @Getter protected OptionType type;

    /**
     * ({@code S}) Price of the underlying asset.
     */
    @Getter protected Number spotPrice;

    /**
     * ({@code K}) Strike/exercise price of the option.
     */
    @Getter protected Number strikePrice;

    /**
     * ({@code τ = T - t}) Time to maturity (time from the start of the contract until it expires).
     */
    @Getter protected Number timeToMaturity;

    /**
     * ({@code σ}) Underlying volatility (standard deviation of log returns).
     */
    @Getter protected Number volatility;

    /**
     * ({@code r}) Annualized risk-free interest rate, continuously compounded.
     */
    @Getter protected Number riskFreeRate;

    /**
     * ({@code q}) Annual dividend yield, continuously compounded.
     */
    @Getter protected Number dividendYield;

    // Fields for calculations to simplify and match the math notation
    protected double S; // spot price
    protected double K; // strike price
    protected double τ; // time to maturity
    protected double σ; // volatility
    protected double r; // risk free rate
    protected double q; // dividend yield
    protected double C̟P̠;
    protected double C̠P̟;

    /**
     * Creates an abstract option with the specified parameters.
     *
     * @param style Style of the option defined by the exercise rights, e.g.&nbsp;European, American.
     * @param type Type of the option, i.e.&nbsp;call or put.
     * @param spotPrice Price of the underlying asset ({@code S > 0}).
     * @param strikePrice Strike/exercise price of the option ({@code K > 0}).
     * @param timeToMaturity Time until maturity/expiration in years ({@code τ = T - t > 0}).
     * @param volatility Underlying volatility ({@code σ > 0}).
     * @param riskFreeRate Annualized risk-free interest rate continuously compounded ({@code r}).
     * @param dividendYield Annual dividend yield continuously compounded ({@code q}).
     * @throws NullPointerException if any of the arguments are null
     * @throws IllegalArgumentException if {@code spotPrice}, {@code strikePrice}, {@code timeToMaturity}, or {@code volatility} are not greater than zero
     */
    public AbstractOption(
        @NonNull OptionStyle style,
        @NonNull OptionType type,
        @NonNull Number spotPrice,
        @NonNull Number strikePrice,
        @NonNull Number timeToMaturity,
        @NonNull Number volatility,
        @NonNull Number riskFreeRate,
        @NonNull Number dividendYield
    ) throws IllegalArgumentException, NullPointerException {
        this.checkGreaterThanZero(spotPrice, "S");
        this.checkGreaterThanZero(strikePrice, "K");
        this.checkGreaterThanZero(timeToMaturity, "T");
        this.checkGreaterThanZero(volatility, "σ");

        this.style = style;
        this.setType(type);
        this.setSpotPrice(spotPrice);
        this.setStrikePrice(strikePrice);
        this.setTimeToMaturity(timeToMaturity);
        this.setVolatility(volatility);
        this.setRiskFreeRate(riskFreeRate);
        this.setDividendYield(dividendYield);
    }

    public double exerciseValue(double spotPrice) {
        return Math.max(0.0, this.typeFactor() * (spotPrice - this.K));
    }

    //region getters and setters
    //----------------------------------------------------------------------

    public double getDoubleSpotPrice() {
        return this.S;
    }

    public double getDoubleStrikePrice() {
        return this.K;
    }

    public double getDoubleTimeToMaturity() {
        return this.τ;
    }

    public double getDoubleVolatility() {
        return this.σ;
    }

    public double getDoubleRiskFreeRate() {
        return this.r;
    }

    public double getDoubleDividendYield() {
        return this.q;
    }

    /**
     * TODO.
     */
    public void setType(@NonNull OptionType type) {
        this.type = type;
        this.C̟P̠ = type == OptionType.CALL ? 1d : -1d;
        this.C̠P̟ = type == OptionType.CALL ? -1d : 1d;
    }

    public void setSpotPrice(@NonNull Number spotPrice) {
        this.spotPrice = spotPrice;
        this.S = spotPrice.doubleValue();
    }

    public void setStrikePrice(@NonNull Number strikePrice) {
        this.strikePrice = strikePrice;
        this.K = strikePrice.doubleValue();
    }

    public void setTimeToMaturity(@NonNull Number timeToMaturity) {
        this.timeToMaturity = timeToMaturity;
        this.τ = timeToMaturity.doubleValue();
    }

    public void setVolatility(@NonNull Number volatility) {
        this.volatility = volatility;
        this.σ = volatility.doubleValue();
    }

    public void setRiskFreeRate(@NonNull Number riskFreeRate) {
        this.riskFreeRate = riskFreeRate;
        this.r = riskFreeRate.doubleValue();
    }

    public void setDividendYield(@NonNull Number dividendYield) {
        this.dividendYield = dividendYield;
        this.q = dividendYield.doubleValue();
    }

    //----------------------------------------------------------------------
    //endregion getters and setters

    protected Map<String, String> baseOptionParameters() {
        return Map.ofEntries(
            Map.entry(LATEX_S.trim(), this.spotPrice.toString()),
            Map.entry(LATEX_K.trim(), this.strikePrice.toString()),
            Map.entry(LATEX_τ.trim(), this.timeToMaturity.toString()),
            Map.entry(LATEX_σ.trim(), this.volatility.toString()),
            Map.entry(LATEX_r.trim(), this.riskFreeRate.toString()),
            Map.entry(LATEX_q.trim(), this.dividendYield.toString())
        );
    }

    /**
     * Returns 1 for a call option, -1 for a put option.
     *
     * @return type factor
     */
    protected double typeFactor() {
        return this.type == OptionType.CALL ? 1 : -1;
    }

    /**
     * Returns "C" for a call option, "P" for a put option.
     *
     * @return type parameter
     */
    protected String typeParameterLatex() {
        return this.type == OptionType.CALL ? " C " : " P ";
    }

    //region constants
    //----------------------------------------------------------------------

    protected static final String LATEX_S = " S ";
    protected static final String LATEX_K = " K ";
    protected static final String LATEX_τ = " \\tau ";
    protected static final String LATEX_σ = " \\sigma ";
    protected static final String LATEX_r = " r ";
    protected static final String LATEX_q = " q ";

    //----------------------------------------------------------------------
    //endregion constants

    private void checkGreaterThanZero(Number value, String name) throws IllegalArgumentException {
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
    }
}
