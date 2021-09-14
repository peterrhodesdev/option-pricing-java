package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Vanilla European option.
 */
public class EuropeanOption extends AbstractAnalyticalOption {
    
    private NormalDistribution N;

    /**
     * Creates a vanilla European option with the specified parameters.
     *
     * @see AbstractAnalyticalOption#AbstractAnalyticalOption(OptionStyle.EUROPEAN, OptionType, double, double, double, double, double, double)
     */
    public EuropeanOption(OptionType type, double S, double K, double T, double vol, double r, double q) throws IllegalArgumentException {
        super(OptionStyle.EUROPEAN, type, S, K, T, vol, r, q);
        this.N = new NormalDistribution();
    }

    /**
     * Calculates the values of d₁ and d₂ in the Black-Scholes formula.
     */
    private double d(int i) {
        return 1 / (this.vol * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r - this.q + (i == 1 ? 1 : -1) * Math.pow(this.vol, 2) / 2) * this.T);
    }

    //region price
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double price() {
        return this.type == OptionType.CALL ? this.callPrice() : this.putPrice();
    }

    private double callPrice() {
        return this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d(1)) - this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d(2));
    }

    private double putPrice() {
        return this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d(2)) - this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d(1));
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
        return this.type == OptionType.CALL ? this.callDelta() : this.putDelta();
    }

    private double callDelta() {
        return Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d(1));
    }

    private double putDelta() {
        return -Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d(1));
    }

    //----------------------------------------------------------------------
    //endregion

    /**
     * {@inheritDoc}
     */
    @Override
    public double gamma() {
        return Math.exp(-this.q * this.T) * this.N.density(this.d(1)) / (this.S * this.vol * Math.sqrt(this.T));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double vega() {
        return this.S * Math.exp(-this.q * this.T) * this.N.density(this.d(1)) * Math.sqrt(this.T);
    }

    //region theta
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double theta() {
        double typeFactor = this.type == OptionType.CALL ? 1 : -1;
        double term1 = -Math.exp(-this.q * this.T) * (this.S * this.N.density(this.d(1)) * this.vol) / (2d * Math.sqrt(this.T));
        double term2 = this.r * this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(typeFactor * this.d(2));
        double term3 = this.q * this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(typeFactor * this.d(1));
        return term1 - (typeFactor *  term2) + (typeFactor * term3);
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
        return this.type == OptionType.CALL ? this.callRho() : this.putRho();
    }

    private double callRho() {
        return this.K * this.T * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d(2));
    }

    private double putRho() {
        return -this.K * this.T * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d(2));
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
