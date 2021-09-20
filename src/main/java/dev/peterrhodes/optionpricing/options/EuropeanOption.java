package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.common.NotYetImplementedException;
import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CalculationModel;
import dev.peterrhodes.optionpricing.utils.FormulaUtils;
import dev.peterrhodes.optionpricing.utils.LatexUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Vanilla European option.&nbsp;The value of the option and it's greeks can be calculated analytically using the <a href="https://www.jstor.org/stable/1831029?origin=JSTOR-pdf">Black-Scholes model</a>
 */
public final class EuropeanOption extends AbstractAnalyticalOption {

    //region constructors
    //----------------------------------------------------------------------

    private EuropeanOption(OptionType type, Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) throws IllegalArgumentException, NullPointerException {
        super(OptionStyle.EUROPEAN, type, spotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    /**
     * Creates a vanilla European call option.&nbsp;The option's style defaults to {@link OptionStyle#EUROPEAN}, and the option's type defaults to {@link OptionType#CALL}.&nbsp;For a description of the other arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    public static EuropeanOption createCall(Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) throws IllegalArgumentException, NullPointerException {
        return new EuropeanOption(OptionType.CALL, spotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    /**
     * Creates a vanilla European put option.&nbsp;The option's style defaults to {@link OptionStyle#EUROPEAN}, and the option's type defaults to {@link OptionType#PUT}.&nbsp;For a description of the other arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    public static EuropeanOption createPut(Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) throws IllegalArgumentException, NullPointerException {
        return new EuropeanOption(OptionType.PUT, spotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
    }

    //----------------------------------------------------------------------
    //endregion constructors

    //region d₁, d₂
    //----------------------------------------------------------------------

    /**
     * Calculates the values of d₁ and d₂ in the Black-Scholes formula.
     *
     * @return dᵢ
     */
    public double d(int i) {
        return (Math.log(this.S / this.K) + (this.r - this.q + (i == 1 ? 1 : -1) * Math.pow(this.σ, 2) / 2) * this.τ) / (this.σ * Math.sqrt(this.τ));
    }

    /**
     * Returns the details of the dᵢ calculation step for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The parts of the calculation step are:</p>
     * <ol start="0">
     *   <li>symbol</li>
     *   <li>equation</li>
     *   <li>option parameters substituted into the equation</li>
     *   <li>value</li>
     * </ol>
     */
    public String[] dCalculationStep(int i) {
        String lhs = this.dParameterLatex(i).trim();

        // RHS
        String iFactor = i == 1 ? " + " : " - ";
        String rhsNumerator = LatexUtils.naturalLogarithm(LatexUtils.fraction(LATEX_S, LATEX_K))
            + LatexUtils.subFormula(LATEX_r + " - " + LATEX_q + iFactor + LatexUtils.half(LatexUtils.squared(LATEX_σ)), LatexDelimeterType.PARENTHESIS) + LATEX_τ;
        String rhsDenominator = LatexUtils.MATH_SYMBOL_GREEK_LETTER_SIGMA_LOWERCASE + LatexUtils.squareRoot(LATEX_τ);
        String rhs = LatexUtils.fraction(rhsNumerator, rhsDenominator);

        List<String> parts = new ArrayList();
        parts.add(lhs);
        if (i == 2) {
            parts.add(this.dParameterLatex(1) + " - " + LATEX_σ + LatexUtils.squareRoot(LATEX_τ));
        }
        parts.add(rhs);
        
        return FormulaUtils.solve(parts.toArray(String[]::new), this.baseCalculationInputs(LatexDelimeterType.NONE), this.roundCalculationStepValue(this.d(i)));
    }

    private String dParameterLatex(int i) {
        return String.format(" d_%d ", i);
    }

    private EquationInput[] dEquationSubstitutionValues() {
        return new EquationInput[] {
            this.dEquationSubstitutionValue(1),
            this.dEquationSubstitutionValue(2)
        };
    }

    private EquationInput dEquationSubstitutionValue(int i) {
        return new EquationInput.Builder(this.dParameterLatex(i).trim())
            .withNumberValue(this.d(i))
            .withPrecision(this.calculationStepPrecision, this.calculationStepRoundingMethod)
            .build();
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
        return this.S * Math.exp(-this.q * this.τ) * this.N_at(this.d(1)) - this.K * Math.exp(-this.r * this.τ) * this.N_at(this.d(2));
    }

    private double pricePut() {
        return this.K * Math.exp(-this.r * this.τ) * this.N_at(-this.d(2)) - this.S * Math.exp(-this.q * this.τ) * this.N_at(-this.d(1));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationModel priceCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion price

    //region delta
    //----------------------------------------------------------------------

    @Override
    public double delta() {
        return this.typeFactor() * Math.exp(-this.q * this.τ) * this.N_at(this.typeFactor() * this.d(1));
    }

    /**
     * Returns the details of the delta (Δ) calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal CDF at d₁ (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
     *   <li>delta (Δ)
     *     <ol start="0">
     *       <li>symbol</li>
     *       <li>PDE</li>
     *       <li>equation</li>
     *       <li>option parameters and value of d₁ substituted in</li>
     *       <li>value</li>
     *     </ol>
     *   </li>
     * </ol>
     */
    @Override
    public CalculationModel deltaCalculation() {
        /*int i = 1;
        String[] d1Step = this.dCalculationStep(i);
        String[] nd1Step = this.standardNormalCdfCalculationStep(this.dParameterLatex(i), this.d(i));
*/
        // delta
        double answer = this.delta();
        String[] finalStep = this.finalCalculationStep(this.deltaFormula(), answer);
/*        
        EquationInput[] inputs = Stream.concat(
                Arrays.stream(this.baseCalculationInputs(LatexDelimeterType.PARENTHESIS)),
                Arrays.stream(new EquationInput[] { this.dEquationSubstitutionValue(1) })
            )
            .toArray(EquationInput[]::new);
        String[] finalStep = FormulaUtils.solve(this.deltaFormula(), inputs, this.roundCalculationStepValue(answer));
*/
        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(1),
                this.N_at_d_calculationStep(1),
                finalStep
            }, answer);
    }

    private String[] deltaFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterLatex(), LATEX_S);

        // RHS
        String rhs = this.dividendDiscountFactorLatex();
        rhs += this.type == OptionType.CALL
            ? standardNormalCdfLatex(this.dParameterLatex(1))
            : LatexUtils.subFormula(standardNormalCdfLatex(this.dParameterLatex(1)) + " - 1", LatexDelimeterType.PARENTHESIS);
/*
        List<String> parts = Arrays.stream(new String[] { LATEX_Δ, lhs }).collect(Collectors.toList());
        if (this.type == OptionType.PUT) {
            parts.add("-" + this.dividendDiscountFactor() + standardNormalCdfLatex("-" + this.dParameterLatex(1)));
        }
        parts.add(rhs);
*/        
        //return parts.toArray(String[]::new);
        return new String[] { LATEX_Δ.trim(), lhs, rhs };
    }

    //----------------------------------------------------------------------
    //endregion delta

    //region gamma
    //----------------------------------------------------------------------

    @Override
    public double gamma() {
        return Math.exp(-this.q * this.τ) * this.N.density(this.d(1)) / (this.S * this.σ * Math.sqrt(this.τ));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationModel gammaCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion gamma

    //region vega
    //----------------------------------------------------------------------

    @Override
    public double vega() {
        return this.S * Math.exp(-this.q * this.τ) * this.N.density(this.d(1)) * Math.sqrt(this.τ);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationModel vegaCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion vega

    //region theta
    //----------------------------------------------------------------------

    @Override
    public double theta() {
        double term1 = -Math.exp(-this.q * this.τ) * (this.S * this.N.density(this.d(1)) * this.σ) / (2d * Math.sqrt(this.τ));
        double term2 = this.r * this.K * Math.exp(-this.r * this.τ) * this.N_at(this.typeFactor() * this.d(2));
        double term3 = this.q * this.S * Math.exp(-this.q * this.τ) * this.N_at(this.typeFactor() * this.d(1));
        return term1 - (this.typeFactor() *  term2) + (this.typeFactor() * term3);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationModel thetaCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion theta

    //region rho
    //----------------------------------------------------------------------

    @Override
    public double rho() {
        return this.typeFactor() * this.K * this.τ * Math.exp(-this.r * this.τ) * this.N_at(this.typeFactor() * this.d(2));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CalculationModel rhoCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion rho

    /**
     * Returns a list of the parameters/variables used to define the option.&nbsp;The key is the latex notation for the parameter and the value is its number value.
     *
     * @return option parameters list
     * <ol start="0">
     *   <li>spot price</li>
     *   <li>strike price</li>
     *   <li>time to maturity</li>
     *   <li>volatility</li>
     *   <li>risk-free rate</li>
     *   <li>dividend yield</li>
     * </ol>
     */
    public Map<String, String> optionParameters() {
        return this.baseOptionParameters();
    }

    private String[] N_at_d_calculationStep(int i) {
        return this.standardNormalCdfCalculationStep(this.dParameterLatex(i), this.d(i));
    }

    private String[] finalCalculationStep(String[] formula, double answer) {
        EquationInput[] inputs = Stream.concat(
            Arrays.stream(this.baseCalculationInputs(LatexDelimeterType.PARENTHESIS)),
            Arrays.stream(this.dEquationSubstitutionValues())
        ).toArray(EquationInput[]::new);

        return FormulaUtils.solve(formula, inputs, this.roundCalculationStepValue(answer));
    }
}
