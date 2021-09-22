package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.Option;
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
     * Type of the option, i.e.&nbsp;call or put.
     */
    @Getter protected OptionType type;

    // No lombok getter annotation as return the double value
    protected Number spotPrice;
    protected Number strikePrice;
    protected Number timeToMaturity;
    protected Number volatility;
    protected Number riskFreeRate;
    protected Number dividendYield;

    // Fields for calculations to simplify and match the math notation
    protected double S; // spot price
    protected double K; // strike price
    protected double τ; // time to maturity
    protected double σ; // volatility
    protected double r; // risk free rate
    protected double q; // dividend yield
    protected double C̟P̠; // +1 for call, -1 for put
    protected double C̠P̟; // -1 for call, +1 for put

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
        this.style = style;
        this.setType(type);
        this.setSpotPrice(spotPrice);
        this.setStrikePrice(strikePrice);
        this.setTimeToMaturity(timeToMaturity);
        this.setVolatility(volatility);
        this.setRiskFreeRate(riskFreeRate);
        this.setDividendYield(dividendYield);
    }

    @Override
    public double exerciseValue(double spotPrice) {
        return Math.max(0.0, this.typeFactor() * (spotPrice - this.K));
    }

    //region getters and setters
    //----------------------------------------------------------------------

    /**
     * Gets the spot price.
     *
     * @return Price of the underlying asset ({@code S > 0}).
     */
    public double getSpotPrice() {
        return this.S;
    }

    /**
     * Gets the strike price.
     *
     * @return Strike/exercise price of the option ({@code K > 0}).
     */
    public double getStrikePrice() {
        return this.K;
    }

    /**
     * Gets the time to maturity.
     *
     * @return Time until maturity/expiration in years ({@code τ = T - t > 0}).
     */
    public double getTimeToMaturity() {
        return this.τ;
    }

    /**
     * Gets the volatility.
     *
     * @return Underlying volatility ({@code σ > 0}).
     */
    public double getVolatility() {
        return this.σ;
    }

    /**
     * Gets the risk-free rate.
     *
     * @return Annualized risk-free interest rate continuously compounded ({@code r}).
     */
    public double getRiskFreeRate() {
        return this.r;
    }

    /**
     * Gets the dividend yield.
     *
     * @return Annual dividend yield continuously compounded ({@code q}).
     */
    public double getDividendYield() {
        return this.q;
    }

    /**
     * Sets the option type.
     *
     * @param type Type of the option, i.e.&nbsp;call or put.
     * @throws NullPointerException if {@code type} is null
     */
    public void setType(@NonNull OptionType type) throws NullPointerException {
        this.type = type;
        this.C̟P̠ = type == OptionType.CALL ? 1d : -1d;
        this.C̠P̟ = type == OptionType.CALL ? -1d : 1d;
    }

    /**
     * Sets the spot price.
     *
     * @param spotPrice Price of the underlying asset ({@code S > 0}).
     * @throws NullPointerException if {@code spotPrice} is null
     * @throws  IllegalArgumentExceptionif {@code spotPrice} is not greater than zero
     */
    public void setSpotPrice(@NonNull Number spotPrice) throws NullPointerException, IllegalArgumentException {
        this.checkGreaterThanZero(spotPrice, "spot price");
        this.spotPrice = spotPrice;
        this.S = spotPrice.doubleValue();
    }

    /**
     * Sets the strike price.
     *
     * @param strikePrice Strike/exercise price of the option ({@code K > 0}).
     * @throws NullPointerException if {@code strikePrice} is null
     * @throws  IllegalArgumentExceptionif {@code strikePrice} is not greater than zero
     */
    public void setStrikePrice(@NonNull Number strikePrice) throws NullPointerException, IllegalArgumentException {
        this.checkGreaterThanZero(strikePrice, "strike price");
        this.strikePrice = strikePrice;
        this.K = strikePrice.doubleValue();
    }

    /**
     * Sets the time to maturity.
     *
     * @param timeToMaturity Time until maturity/expiration in years ({@code τ = T - t > 0}).
     * @throws NullPointerException if {@code timeToMaturity} is null
     * @throws  IllegalArgumentExceptionif {@code timeToMaturity} is not greater than zero
     */
    public void setTimeToMaturity(@NonNull Number timeToMaturity) throws NullPointerException, IllegalArgumentException {
        this.checkGreaterThanZero(timeToMaturity, "time to maturity");
        this.timeToMaturity = timeToMaturity;
        this.τ = timeToMaturity.doubleValue();
    }

    /**
     * Sets the volatility.
     *
     * @param volatility Underlying volatility ({@code σ > 0}).
     * @throws NullPointerException if {@code volatility} is null
     * @throws  IllegalArgumentExceptionif {@code volatility} is not greater than zero
     */
    public void setVolatility(@NonNull Number volatility) throws NullPointerException, IllegalArgumentException {
        this.checkGreaterThanZero(volatility, "volatility");
        this.volatility = volatility;
        this.σ = volatility.doubleValue();
    }

    /**
     * Sets the risk-free rate.
     *
     * @param riskFreeRate Annualized risk-free interest rate continuously compounded ({@code r}).
     * @throws NullPointerException if {@code riskFreeRate} is null
     */
    public void setRiskFreeRate(@NonNull Number riskFreeRate) throws NullPointerException {
        this.riskFreeRate = riskFreeRate;
        this.r = riskFreeRate.doubleValue();
    }

    /**
     * Sets the dividend yield.
     *
     * @param dividendYield Annual dividend yield continuously compounded ({@code q}).
     * @throws NullPointerException if {@code dividendYield} is null
     */
    public void setDividendYield(@NonNull Number dividendYield) throws NullPointerException {
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
