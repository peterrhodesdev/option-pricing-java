package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;

import java.lang.Math;

import org.apache.commons.math3.distribution.NormalDistribution;

public class EuropeanOption implements IOption {

    private OptionType optionType;
    private double S;
    private double K;
    private double T;
    private double σ;
    private double r;

    /**
     * Creates a European option with the specified parameters.
     * @param optionType type of the option (call or put)
     * @param S price of the underlying asset (spot price)
     * @param K strike price of the option (exercise price)
     * @param T time until option expiration (time from the start of the contract until maturity)
     * @param σ standard deviation of the underlying's returns (a measure of volatility)
     * @param r annualized risk-free interest rate, continuously compounded
     * @throws IllegalArgumentException if S, K, T, or σ are not greater than zero
     */
    public EuropeanOption(OptionType optionType, double S, double K, double T, double σ, double r) throws IllegalArgumentException {
        this.optionType = optionType;
        this.S = this.checkGreaterThanZero(S, "S");
        this.K = this.checkGreaterThanZero(K, "K");
        this.T = this.checkGreaterThanZero(T, "T");
        this.σ = this.checkGreaterThanZero(σ, "σ");
        this.r = r;
    }

    private double checkGreaterThanZero(double value, String name) throws IllegalArgumentException {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return value;
    }

    public double analyticalPrice() {
        NormalDistribution N = new NormalDistribution();
        return N.cumulativeProbability(this.d_1()) * this.S - N.cumulativeProbability(this.d_2()) * this.K * Math.exp(-1d * this.r * this.T);
    }

    //private double d_1(double S, double K, double T, double σ, double r) {
    private double d_1() {
        return 1d / (this.σ * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r + Math.pow(this.σ, 2d) / 2d) * this.T);
    }

    //private double d_2(double d_1, double T, double σ) {
    private double d_2() {
        return this.d_1() - this.σ * Math.sqrt(this.T);
    }
}
