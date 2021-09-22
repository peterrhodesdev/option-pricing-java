package dev.peterrhodes.optionpricing.contracts;

import dev.peterrhodes.optionpricing.Contract;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import lombok.NonNull;

/**
 * TODO.
 */
public abstract class AbstractContract implements Contract {

    /**
     * Style of the option, usually defined by the exercise rights, e.g.&nbsp;European, American.
     */
    private OptionStyle style;

    /**
     * Type of the option, i.e.&nbsp;call or put.
     */
    private OptionType type;

    private Number spotPrice;
    private Number strikePrice;
    private Number timeToMaturity;
    private Number volatility;
    private Number riskFreeRate;
    private Number dividendYield;

    /**
     * TODO.
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
    public AbstractContract(
        @NonNull OptionStyle style,
        @NonNull OptionType type,
        @NonNull Number spotPrice,
        @NonNull Number strikePrice,
        @NonNull Number timeToMaturity,
        @NonNull Number volatility,
        @NonNull Number riskFreeRate,
        @NonNull Number dividendYield
    ) throws IllegalArgumentException, NullPointerException {
        checkGreaterThanZero(spotPrice, "spotPrice");
        checkGreaterThanZero(strikePrice, "strikePrice");
        checkGreaterThanZero(timeToMaturity, "timeToMaturity");
        checkGreaterThanZero(volatility, "volatility");

        this.style = style;
        this.type = type;

        this.spotPrice = spotPrice;
        this.strikePrice = strikePrice;
        this.timeToMaturity = timeToMaturity;
        this.volatility = volatility;
        this.riskFreeRate = riskFreeRate;
        this.dividendYield = dividendYield;
    }

    @Override
    public double exerciseValue(double time, double spotPrice) {
        if (this.style == OptionStyle.EUROPEAN && time < this.timeToMaturity()) {
            return 0d;
        }
        return Math.max(0d, this.typeFactor() * (spotPrice - this.strikePrice()));
    }

    @Override
    public double spotPrice() {
        return this.spotPrice.doubleValue();
    }

    @Override
    public double strikePrice() {
        return this.strikePrice.doubleValue();
    }

    @Override
    public double timeToMaturity() {
        return this.timeToMaturity.doubleValue();
    }

    @Override
    public double volatility() {
        return this.volatility.doubleValue();
    }

    @Override
    public double riskFreeRate() {
        return this.riskFreeRate.doubleValue();
    }

    @Override
    public double dividendYield() {
        return this.dividendYield.doubleValue();
    }

    private void checkGreaterThanZero(Number value, String name) throws IllegalArgumentException {
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
    }

    /**
     * Returns 1 for a call option, -1 for a put option.
     *
     * @return type factor
     */
    private double typeFactor() {
        return this.type == OptionType.CALL ? 1 : -1;
    }
}
