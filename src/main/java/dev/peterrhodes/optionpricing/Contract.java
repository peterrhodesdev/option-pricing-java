package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;

/**
 * TODO.
 */
public interface Contract {

    /**
     * TODO.
     */
    public double exerciseValue(double time, double spotPrice);

    /**
     * Returns the spot price.
     *
     * @return Price of the underlying asset ({@code S > 0}).
     */
    public double spotPrice();

    /**
     * Returns the strike price.
     *
     * @return Strike/exercise price of the option ({@code K > 0}).
     */
    public double strikePrice();

    /**
     * Returns the time to maturity.
     *
     * @return Time until maturity/expiration in years ({@code τ = T - t > 0}).
     */
    public double timeToMaturity();

    /**
     * Returns the volatility.
     *
     * @return Underlying volatility ({@code σ > 0}).
     */
    public double volatility();

    /**
     * Returns the risk-free rate.
     *
     * @return Annualized risk-free interest rate continuously compounded ({@code r}).
     */
    public double riskFreeRate();

    /**
     * Returns the dividend yield.
     *
     * @return Annual dividend yield continuously compounded ({@code q}).
     */
    double dividendYield();
}
