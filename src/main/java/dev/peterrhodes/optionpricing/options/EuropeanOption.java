package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;

import java.lang.Math;

import org.apache.commons.math3.distribution.NormalDistribution;

public class EuropeanOption implements IOption {

    private OptionType optionType;
    private double S;
    private double K;
    private double T;
    private double v;
    private double r;
    private double q;
    private NormalDistribution N;

    /**
     * Creates a European option with the specified parameters.
     * @param optionType type of the option (call or put)
     * @param S price of the underlying asset (spot price)
     * @param K strike price of the option (exercise price)
     * @param T time until option expiration (time from the start of the contract until maturity)
     * @param v (σ) underlying volatility (standard deviation of log returns)
     * @param r annualized risk-free interest rate, continuously compounded
     * @param q continuous dividend yield
     * @throws IllegalArgumentException if S, K, T, or v are not greater than zero
     */
    public EuropeanOption(OptionType optionType, double S, double K, double T, double v, double r, double q) throws IllegalArgumentException {
        this.optionType = optionType;
        this.S = this.checkGreaterThanZero(S, "S");
        this.K = this.checkGreaterThanZero(K, "K");
        this.T = this.checkGreaterThanZero(T, "T");
        this.v = this.checkGreaterThanZero(v, "v");
        this.r = r;
        this.q = q;
        this.N = new NormalDistribution();
    }

    private double checkGreaterThanZero(double value, String name) throws IllegalArgumentException {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return value;
    }

    /* analyticalPrice */

    @Override
    public double analyticalPrice() {
        if (this.optionType == OptionType.CALL) {
            return this.analyticalCallPrice();
        }
        return this.analyticalPutPrice();
    }

    private double analyticalCallPrice() {
        return this.discountFactor() * (this.forwardPrice() * this.N.cumulativeProbability(this.d_i(1)) - this.K * this.N.cumulativeProbability(this.d_i(2)));
    }

    private double analyticalPutPrice() {
        return this.discountFactor() * (this.K * this.N.cumulativeProbability(-this.d_i(2)) - this.forwardPrice() * this.N.cumulativeProbability(-this.d_i(1)));
    }

    private double discountFactor() {
        return Math.exp(-this.r * this.T);
    }

    private double forwardPrice() {
        return this.S * Math.exp((this.r - this.q) * this.T);
    }

    private double d_i(int i) {
        double sign = i == 1 ? 1d : -1d;
        return 1d / (this.v * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r - this.q + sign * Math.pow(this.v, 2d) / 2d) * this.T);
    }
}
