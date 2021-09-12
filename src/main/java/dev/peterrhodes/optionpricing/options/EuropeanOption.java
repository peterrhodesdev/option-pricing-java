package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculation;

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

    //region analytical
    //----------------------------------------------------------------------

    private double d_i(int i) {
        double sign = i == 1 ? 1d : -1d;
        return 1d / (this.v * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r - this.q + sign * Math.pow(this.v, 2d) / 2d) * this.T);
    }

    /* price */

    @Override
    public double analyticalPrice() {
        return this.optionType == OptionType.CALL ? this.analyticalCallPrice() : this.analyticalPutPrice();
    }

    private double analyticalCallPrice() {
        return this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d_i(1)) - this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d_i(2));
    }

    private double analyticalPutPrice() {
        return this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d_i(2)) - this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d_i(1));
    }

    /* calculation model */

    public AnalyticalCalculation analyticalCalculation() {
        double price = this.analyticalPrice();
        double delta = this.delta();
        double gamma = this.gamma();
        double vega = this.vega();
        double theta = this.theta();
        double rho = this.rho();
        return new AnalyticalCalculation(price, delta, gamma, vega, theta, rho);
    }

    private double delta() {
        return this.optionType == OptionType.CALL ? this.callDelta() : this.putDelta();
    }

    private double callDelta() {
        return Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d_i(1));
    }

    private double putDelta() {
        return -Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d_i(1));
    }

    private double gamma() {
        return 0.0;
    }

    private double vega() {
        return 0.0;
    }

    private double theta() {
        return 0.0;
    }

    private double rho() {
        return 0.0;
    }

    //----------------------------------------------------------------------
    //endregion
}
