package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionType;

import lombok.Getter;

@Getter
public abstract class AbstractOption implements Option {

    protected OptionType optionType;
    protected double S;
    protected double K;
    protected double T;
    protected double v;
    protected double r;
    protected double q;

    /**
     * Creates an abstract option with the specified parameters.
     * @param optionType type of the option (call or put)
     * @param S price of the underlying asset (spot price)
     * @param K strike price of the option (exercise price)
     * @param T time until option expiration (time from the start of the contract until maturity)
     * @param v (σ) underlying volatility (standard deviation of log returns)
     * @param r annualized risk-free interest rate, continuously compounded
     * @param q continuous dividend yield
     * @throws IllegalArgumentException if S, K, T, or v are not greater than zero
     */
    public AbstractOption(OptionType optionType, double S, double K, double T, double v, double r, double q) throws IllegalArgumentException {
        this.optionType = optionType;
        this.S = this.checkGreaterThanZero(S, "S");
        this.K = this.checkGreaterThanZero(K, "K");
        this.T = this.checkGreaterThanZero(T, "T");
        this.v = this.checkGreaterThanZero(v, "v");
        this.r = r;
        this.q = q;
    }

    private double checkGreaterThanZero(double value, String name) throws IllegalArgumentException {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return value;
    }
}
