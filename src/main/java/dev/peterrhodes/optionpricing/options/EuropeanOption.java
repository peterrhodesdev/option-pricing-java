package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.common.NotYetImplementedException;
import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.core.Calculation;
import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.core.Parameter;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.helpers.CalculationHelper;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Vanilla European option.
 */
public class EuropeanOption extends AbstractAnalyticalOption {
    
    private NormalDistribution N;

    /**
     * Parameter containing the LaTeX equation and description for the standard normal cumulative distribution function.
     */
    private Parameter cdf = new Parameter(
        "\\mathrm N(x) = \\frac{1}{\\sqrt{2\\pi}} \\int_{-\\infty}^{x} e^{-\\frac{z^2}{2}} dz",
        "standard normal cumulative distribution function"
    );

    /**
     * Parameter containing the LaTeX equation and description for the standard normal probability density function.
     */
    private Parameter pdf = new Parameter(
        "\\mathrm N'(x) = \\frac{d{\\mathrm N(x)}}{dx} = \\frac{1}{\\sqrt{2\\pi}} e^{-\\frac{x^2}{2}}",
        "standard normal probability density function"
    );

    /**
     * Creates a vanilla European option with the specified parameters.&nbsp;{@link dev.peterrhodes.optionpricing.core.AbstractOption#style} defaults to {@link OptionStyle#EUROPEAN}.
     *
     * @param type {@link dev.peterrhodes.optionpricing.core.AbstractOption#type}
     * @param S {@link dev.peterrhodes.optionpricing.core.AbstractOption#S}
     * @param K {@link dev.peterrhodes.optionpricing.core.AbstractOption#K}
     * @param T {@link dev.peterrhodes.optionpricing.core.AbstractOption#T}
     * @param vol {@link dev.peterrhodes.optionpricing.core.AbstractOption#vol}
     * @param r {@link dev.peterrhodes.optionpricing.core.AbstractOption#r}
     * @param q {@link dev.peterrhodes.optionpricing.core.AbstractOption#q}
     * @throws IllegalArgumentException from {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     */
    public EuropeanOption(OptionType type, double S, double K, double T, double vol, double r, double q) throws IllegalArgumentException {
        super(OptionStyle.EUROPEAN, type, S, K, T, vol, r, q);
        this.N = new NormalDistribution();
    }

    //region private helpers
    //----------------------------------------------------------------------

    /**
     * Returns 1 for a call option, -1 for a put option.
     *
     * @return type factor
     */
    private double typeFactor() {
        return this.type == OptionType.CALL ? 1 : -1;
    }

    /**
     * Returns "C" for a call option, "P" for a put option.
     *
     * @return type parameter
     */
    private String typeParameter() {
        return this.type == OptionType.CALL ? "C" : "P";
    }

    private List<Parameter> baseFormulaParameters() {
        List<Parameter> params = new ArrayList<Parameter>();

        if (this.type == OptionType.CALL) {
            params.add(new Parameter("C", "call option price"));
        } else {
            params.add(new Parameter("P", "put option price"));
        }

        params.add(new Parameter("S_0", "price of the underlying asset at time 0"));
        params.add(new Parameter("K", "strike price of the option (exercise price)"));
        params.add(new Parameter("T", "time until option expiration (time from the start of the contract until maturity)"));
        params.add(new Parameter("\\sigma", "underlying volatility (standard deviation of log returns)"));
        params.add(new Parameter("r", "annualized risk-free interest rate, continuously compounded"));
        params.add(new Parameter("q", "continuous dividend yield"));

        return params;
    }

    private List<EquationInput> baseCalculationInputs() {
        List<EquationInput> inputs = new ArrayList();

        inputs.add(new EquationInput("S_0", Double.toString(this.S)));
        inputs.add(new EquationInput("K", Double.toString(this.K)));
        inputs.add(new EquationInput("T", Double.toString(this.T)));
        inputs.add(new EquationInput("\\sigma", Double.toString(this.vol)));
        inputs.add(new EquationInput("r", Double.toString(this.r)));
        inputs.add(new EquationInput("q", Double.toString(this.q)));

        return inputs;
    }

    //----------------------------------------------------------------------
    //endregion private helpers

    //region d₁, d₂
    //----------------------------------------------------------------------

    /**
     * Calculates the values of d₁ and d₂ in the Black-Scholes formula.
     *
     * @return d_i
     */
    private double d(int i) {
        return 1 / (this.vol * Math.sqrt(this.T)) * (Math.log(this.S / this.K) + (this.r - this.q + (i == 1 ? 1 : -1) * Math.pow(this.vol, 2) / 2) * this.T);
    }

    private Formula dFormula(int i) {
        String lhs = String.format("d_%d", i);
        String iFactor = i == 1 ? "+" : "-";
        String rhs = "\\frac{\\ln{\\left(\\frac{S_0}{K}\\right)} + \\left(r " + iFactor + " \\frac{\\sigma^2}{2} \\right) T}{\\sigma \\sqrt{T}}";
        String alt = i == 2 ? "d_1 - \\sigma \\sqrt{T}" : null;
        return new Formula(lhs, rhs, alt);
    }

    //----------------------------------------------------------------------
    //endregion d₁, d₂

    //region price
    //----------------------------------------------------------------------

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
    public String[] priceFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] priceCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion price

    //region delta
    //----------------------------------------------------------------------

    @Override
    public double delta() {
        return this.typeFactor() * Math.exp(-this.q * this.T) * this.N.cumulativeProbability(this.typeFactor() * this.d(1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Formula deltaFormula() {
        String lhs = "\\frac{\\partial " + this.typeParameter() + "}{\\partial S}";
        String rhs = this.type == OptionType.CALL ? "\\mathrm N(d_1)" : "-\\mathrm N(-d_1)";
        String alt = this.type == OptionType.CALL ? null : "\\mathrm N(d_1) - 1";

        List<String> whereComponents = new ArrayList();
        whereComponents.add(this.dFormula(1).build());

        List<Parameter> parameters = this.baseFormulaParameters();
        parameters.add(this.cdf);

        return new Formula(lhs, rhs, alt, whereComponents, parameters);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Calculation deltaCalculation() {
        List<EquationInput> inputs = this.baseCalculationInputs();

        List<String> steps = new ArrayList();

        // d_1
        String d1Value = Double.toString(this.d(1));
        String d1CalculationStep = CalculationHelper.solveFormula(this.dFormula(1), inputs, d1Value);
        steps.add(d1CalculationStep);
        
        // TODO N

        // delta
        List<EquationInput> deltaInputs = new ArrayList();
        deltaInputs.add(new EquationInput("d_1", d1Value));
        String deltaValue = Double.toString(this.delta());
        String deltaCalculationStep = CalculationHelper.solveFormula(this.deltaFormula(), deltaInputs, deltaValue);
        steps.add(deltaCalculationStep);

        String answer = deltaValue;

        return new Calculation(inputs, steps, answer);
    }

    //----------------------------------------------------------------------
    //endregion delta

    //region gamma
    //----------------------------------------------------------------------

    @Override
    public double gamma() {
        return Math.exp(-this.q * this.T) * this.N.density(this.d(1)) / (this.S * this.vol * Math.sqrt(this.T));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] gammaFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] gammaCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion gamma

    //region vega
    //----------------------------------------------------------------------

    @Override
    public double vega() {
        return this.S * Math.exp(-this.q * this.T) * this.N.density(this.d(1)) * Math.sqrt(this.T);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] vegaFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] vegaCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion vega

    //region theta
    //----------------------------------------------------------------------

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
    public String[] thetaFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] thetaCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion theta

    //region rho
    //----------------------------------------------------------------------

    @Override
    public double rho() {
        return this.typeFactor() * this.K * this.T * Math.exp(-this.r * this.T) * this.N.cumulativeProbability(this.typeFactor() * this.d(2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] rhoFormula() {
        throw new NotYetImplementedException();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] rhoCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion rho

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
