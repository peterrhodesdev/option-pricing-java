package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.common.NotYetImplementedException;
import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.core.Calculation;
import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.core.Formula;
import dev.peterrhodes.optionpricing.core.Parameter;
import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.helpers.CalculationHelper;
import dev.peterrhodes.optionpricing.helpers.LatexHelper;
import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Vanilla European option.&nbsp;The value of the option and it's greeks can be calculated analytically using the <a href="https://www.jstor.org/stable/1831029?origin=JSTOR-pdf">Black-Scholes model</a>
 */
public class EuropeanOption extends AbstractAnalyticalOption {
    
    private NormalDistribution N;

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
        String lhs = this.dParameterNotation(i);
        String iFactor = i == 1 ? " + " : " - ";
        String rhsNumerator = LatexHelper.naturalLogarithm(LatexHelper.fraction(NOTATION_S, NOTATION_K))
            + LatexHelper.subFormula(NOTATION_R + iFactor + LatexHelper.half(LatexHelper.squared(NOTATION_VOL)), LatexDelimeterType.PARENTHESIS) + NOTATION_T;
        String rhsDenominator = LatexHelper.MATH_SYMBOL_GREEK_LETTER_SIGMA_LOWERCASE + LatexHelper.squareRoot(NOTATION_T);
        String rhs = LatexHelper.fraction(rhsNumerator, rhsDenominator);

        List<String> steps = new ArrayList();
        if (i == 2) {
            steps.add(this.dParameterNotation(1) + " - " + NOTATION_VOL + LatexHelper.squareRoot(NOTATION_T));
        }
        
        return new Formula(lhs, rhs, steps);
    }

    private String dParameterNotation(int i) {
        return String.format("d_%d", i);
    }

    private double dStandardNormalCdf(int i, double dFactor) {
        return this.N.cumulativeProbability(dFactor * this.d(i));
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
        return this.typeFactor() * Math.exp(-this.q * this.T) * this.dStandardNormalCdf(1, this.typeFactor());
    }

    /**
     * Formula for the delta (Δ) of a European option.&nbsp;For a list of parameters used in the formula see {@link #optionParameters}, and for a list of the functions see {@link #calculationFunctions}.
     * <p>The "where" components include the formulas for:</p>
     * <ol start="0">
     *   <li>d₁</li>
     * </ol>
     */
    @SuppressWarnings("checkstyle:variabledeclarationusagedistance")
    @Override
    public Formula deltaFormula() {
        String lhs = LatexHelper.partialDerivative(this.typeParameterNotation(), NOTATION_S);
        String factor = LatexHelper.exponential("-" + NOTATION_Q + NOTATION_T);
        String rhs = this.type == OptionType.CALL
            ? factor + notationStandardNormalCdf(this.dParameterNotation(1))
            : "-" + factor + notationStandardNormalCdf("-" + this.dParameterNotation(1));

        List<String> steps = new ArrayList();
        if (this.type == OptionType.PUT) {
            steps.add(notationStandardNormalCdf(this.dParameterNotation(1)) + " - 1");
        }

        List<String> whereComponents = new ArrayList();
        whereComponents.add(this.dFormula(1).build());

        return new Formula(lhs, rhs, steps, whereComponents);
    }

    /**
     * Calculation for the delta (Δ) of a European option.
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁</li>
     *   <li>call: N(d₁), put: N(-d₁)</li>
     *   <li>Δ</li>
     * </ol>
     */
    @Override
    public Calculation deltaCalculation() {
        List<EquationInput> inputs = this.baseCalculationInputs();

        List<String> steps = new ArrayList();

        // d_1
        String d1Value = Double.toString(this.d(1));
        String d1CalculationStep = CalculationHelper.solveFormula(this.dFormula(1), inputs, d1Value);
        steps.add(d1CalculationStep);
        
        List<EquationInput> deltaInputs = new ArrayList(); // 
        deltaInputs.add(new EquationInput(this.dParameterNotation(1), d1Value));

        // N (TODO refactor)
        String nLhs = this.type == OptionType.CALL
            ? notationStandardNormalCdf(this.dParameterNotation(1))
            : notationStandardNormalCdf("-" + this.dParameterNotation(1));
        String nSubstituted = nLhs + CalculationHelper.substituteValuesIntoEquation(nLhs, deltaInputs);
        String nValue = Double.toString(this.dStandardNormalCdf(1, this.typeFactor()));
        steps.add(nLhs + " = " + nSubstituted + " = " + nValue);

        // delta
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

    /**
     * Returns a list of the functions used in the calculation formulas.
     *
     * @return calculation functions list
     * <ol start="0">
     *   <li>standard normal CDF</li>
     *   <li>standard normal PDF</li>
     * </ol>
     */
    public List<Parameter> calculationFunctions() {
        return this.baseFunctions();
    }

    /**
     * Returns a list of the parameters/variables used to define the option.
     *
     * @return option parameters list
     * <ol start="0">
     *   <li>call or put</li>
     *   <li>spot price</li>
     *   <li>exercise price</li>
     *   <li>time to maturity</li>
     *   <li>volatility</li>
     *   <li>risk-free rate</li>
     *   <li>dividend yield</li>
     * </ol>
     */
    public List<Parameter> optionParameters() {
        return this.baseParameters();
    }

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

    //region private methods
    //----------------------------------------------------------------------

    /**
     * Returns 1 for a call option, -1 for a put option.
     *
     * @return type factor
     */
    private double typeFactor() {
        return this.type == OptionType.CALL ? 1 : -1;
    }

    //----------------------------------------------------------------------
    //endregion private methods
}
