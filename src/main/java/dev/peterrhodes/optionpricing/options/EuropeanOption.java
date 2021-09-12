package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;

import org.apache.commons.math3.distribution.NormalDistribution;

public class EuropeanOption extends AbstractAnalyticalOption {
    
    private NormalDistribution N;

    /**
     * Creates a European option with the specified parameters.
     * @see AbstractAnalyticalOption#AbstractAnalyticalOption(OptionType optionType, double, double, double, double, double, double)
     */
    public EuropeanOption(OptionType optionType, double S, double K, double T, double v, double r, double q) throws IllegalArgumentException {
        super(optionType, S, K, T, v, r, q);
        this.N = new NormalDistribution();
    }

    private double d_i(int i) {
        double sign = i == 1 ? 1d : -1d;
        return 1d / (this.v * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r - this.q + sign * Math.pow(this.v, 2d) / 2d) * this.T);
    }

    //region price
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double price() {
        return this.optionType == OptionType.CALL ? this.callPrice() : this.putPrice();
    }

    private double callPrice() {
        return this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d_i(1)) - this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d_i(2));
    }

    private double putPrice() {
        return this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d_i(2)) - this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d_i(1));
    }

    //----------------------------------------------------------------------
    //endregion

    //region delta
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double delta() {
        return this.optionType == OptionType.CALL ? this.callDelta() : this.putDelta();
    }

    private double callDelta() {
        return Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d_i(1));
    }

    private double putDelta() {
        return -Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d_i(1));
    }

    //----------------------------------------------------------------------
    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public double gamma() {
        return Math.exp(-this.q * this.T) * this.N.density(this.d_i(1)) / (this.S * this.v * Math.sqrt(this.T));
    }

    public double vega() {
        return this.S * Math.exp(-this.q * this.T) * this.N.density(this.d_i(1)) * Math.sqrt(this.T);
    }

    //region theta
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double theta() {
        return this.optionType == OptionType.CALL ? this.callTheta() : this.putTheta();
    }

    private double callTheta() {
        double term1 = -Math.exp(-this.q * this.T) * (this.S * this.N.density(this.d_i(1)) * this.v) / (2d * Math.sqrt(this.T));
        double term2 = this.r * this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d_i(2));
        double term3 = this.q * this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d_i(1));
        return term1 - term2 + term3;
    }

    private double putTheta() {
        double term1 = -Math.exp(-this.q * this.T) * (this.S * this.N.density(this.d_i(1)) * this.v) / (2d * Math.sqrt(this.T));
        double term2 = this.r * this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d_i(2));
        double term3 = this.q * this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d_i(1));
        return term1 + term2 - term3;
    }

    //----------------------------------------------------------------------
    //endregion

    //region rho
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double rho() {
        return this.optionType == OptionType.CALL ? this.callRho() : this.putRho();
    }

    private double callRho() {
        return this.K * this.T * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d_i(2));
    }

    private double putRho() {
        return -this.K * this.T * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d_i(2));
    }

    //----------------------------------------------------------------------
    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public AnalyticalCalculationModel calculation() {
        double price = this.price();
        double delta = this.delta();
        double gamma = this.gamma();
        double vega = this.vega();
        double theta = this.theta();
        double rho = this.rho();
        return new AnalyticalCalculationModel(price, delta, gamma, vega, theta, rho);
    }
}
