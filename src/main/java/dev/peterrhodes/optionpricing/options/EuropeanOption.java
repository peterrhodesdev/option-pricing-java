package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.common.NotYetImplementedException;
import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;
//import java.util.HashMap;
//import java.util.Map;
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

    //region private helpers
    //----------------------------------------------------------------------

    /**
     * Calculates the values of d₁ and d₂ in the Black-Scholes formula.
     *
     * @return d_i
     */
    private double d(int i) {
        return 1 / (this.vol * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r - this.q + (i == 1 ? 1 : -1) * Math.pow(this.vol, 2) / 2) * this.T);
    }

    /**
     * Returns 1 for a call option, -1 for a put option.
     *
     * @return type factor
     */
    private double typeFactor() {
        return this.type == OptionType.CALL ? 1 : -1;
    }
/*
    private String substituteValuesIntoFormula(String formula) {
        String decimalPlaces = "2";
        String wordBoundaryReGex = "\\b";
        String substitutedValues = formula;

        Map<String, Double> parameters = new HashMap();
        parameters.put("S_0", this.S);
        parameters.put("K", this.K);
        parameters.put("T", this.T);
        parameters.put("r", this.r);
        parameters.put("q", this.q);

        for (Map.Entry param : parameters.entrySet()) {
            String regEx = wordBoundaryReGex + param.getKey() + wordBoundaryReGex;
            String value = String.format("%." + decimalPlaces + "f", param.getValue());
            substitutedValues = substitutedValues.replaceAll(regEx, value);
        }
        substitutedValues = substitutedValues.replaceAll("\\\\sigma", String.format("%." + decimalPlaces + "f", this.vol));
        
        return substitutedValues;
    }
*/
    //----------------------------------------------------------------------
    //endregion private helpers

    //region price
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double price() {
        return this.type == OptionType.CALL ? this.priceCall() : this.pricePut();
    }

    private double priceCall() {
        return this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.d(1)) - this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.d(2));
    }

    private double pricePut() {
        return this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(-this.d(2)) - this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(-this.d(1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] priceLatexFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] priceLatexCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion price

    //region delta
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double delta() {
        return this.typeFactor() * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.typeFactor() * this.d(1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] deltaLatexFormula() {
        throw new NotYetImplementedException();
    }
/*
    private String deltaLatexFormulaLhs() {
        return "d_1";
    }

    private String deltaLatexFormulaRhs(boolean substituteValues) {
        String latex = "\\frac{\\ln{\\left(\\frac{S_0}{K}\\right)} + \\left(r + \\frac{\\sigma^2}{2} \\right) T}{\\sigma \\sqrt{T}}";
        if (substituteValues) {
            latex = this.substituteValuesIntoFormula(latex);
        }
        return latex;
    }
*/
    /**
     * {@inheritDoc}
     */
    @Override
    public String[] deltaLatexCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion delta

    //region gamma
    //----------------------------------------------------------------------

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
    public String[] gammaLatexFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] gammaLatexCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion gamma

    //region vega
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double vega() {
        return this.S * Math.exp(-this.q * this.T) * this.N.density(this.d(1)) * Math.sqrt(this.T);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] vegaLatexFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] vegaLatexCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion vega

    //region theta
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double theta() {
        double term1 = -Math.exp(-this.q * this.T) * (this.S * this.N.density(this.d(1)) * this.vol) / (2d * Math.sqrt(this.T));
        double term2 = this.r * this.K * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.typeFactor() * this.d(2));
        double term3 = this.q * this.S * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.typeFactor() * this.d(1));
        return term1 - (this.typeFactor() *  term2) + (this.typeFactor() * term3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] thetaLatexFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] thetaLatexCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion theta

    //region rho
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double rho() {
        return this.typeFactor() * this.K * this.T * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.typeFactor() * this.d(2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] rhoLatexFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] rhoLatexCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion rho

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
