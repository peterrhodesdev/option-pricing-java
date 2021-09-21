package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.core.AbstractAnalyticalOption;
import dev.peterrhodes.optionpricing.core.EquationInput;
import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CalculationModel;
import dev.peterrhodes.optionpricing.utils.FormulaUtils;
import dev.peterrhodes.optionpricing.utils.LatexUtils;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Vanilla European option.
 * <p>The value of a European option and it's greeks can be calculated analytically using the <a href="https://www.jstor.org/stable/1831029">Black-Scholes model</a>. This model was extended by <a href="https://www.jstor.org/stable/3003143">Merton</a> to allow for the inclusion of a continuous dividend yield.</p>
 */
@SuppressWarnings("PMD")
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
     * @throws IllegalArgumentException if i not in {1, 2}
     */
    public double d(int i) throws IllegalArgumentException {
        checkdi(i);

        return (Math.log(this.S / this.K) + (this.r - this.q + (i == 1 ? 1 : -1) * Math.pow(this.σ, 2) / 2) * this.τ)
            / (this.σ * Math.sqrt(this.τ));
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
     *
     * @throws IllegalArgumentException if i not in {1, 2}
     */
    public String[] dCalculationStep(int i) {
        checkdi(i);

        return FormulaUtils.solve(
            this.dFormula(i),
            this.baseCalculationInputs(LatexDelimeterType.NONE),
            this.roundCalculationStepValue(this.d(i))
        );
    }

    private void checkdi(int i) throws IllegalArgumentException {
        if (i != 1 && i != 2) {
            throw new IllegalArgumentException("i must be either 1 or 2");
        }
    }

    private String[] dFormula(int i) {
        String lhs = this.dParameterLatex(i, true).trim();

        // RHS
        String iFactor = i == 1 ? " + " : " - ";
        String rhsNumerator = LatexUtils.naturalLogarithm(LatexUtils.fraction(LATEX_S, LATEX_K))
            + LatexUtils.subFormula(LATEX_r + " - " + LATEX_q + iFactor + LatexUtils.half(LatexUtils.squared(LATEX_σ)), LatexDelimeterType.PARENTHESIS) + LATEX_τ;
        String rhsDenominator = LatexUtils.MATH_SYMBOL_GREEK_LETTER_SIGMA_LOWERCASE + LatexUtils.squareRoot(LATEX_τ);
        String rhs = LatexUtils.fraction(rhsNumerator, rhsDenominator);

        return new String[] { lhs, rhs };
    }

    private String dParameterLatex(int i, boolean positive) {
        return String.format(" " + (positive ? "" : "- ") + "d_%d ", i);
    }

    private EquationInput[] dEquationSubstitutionValues() {
        return new EquationInput[] {
            this.dEquationSubstitutionValue(1),
            this.dEquationSubstitutionValue(2)
        };
    }

    private EquationInput dEquationSubstitutionValue(int i) {
        return new EquationInput.Builder(this.dParameterLatex(i, true).trim())
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
        return this.C̟P̠ * this.S * Math.exp(-this.q * this.τ) * this.N(this.C̟P̠ * this.d(1))
             + this.C̠P̟ * this.K * Math.exp(-this.r * this.τ) * this.N(this.C̟P̠ * this.d(2));
    }

    /**
     * Returns the details of the price calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁ (see {@link #dCalculationStep(int)})</li>
     *   <li>d₂ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal CDF at +d₁ for call and -d₁ for put (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
     *   <li>standard normal CDF at +d₂ for call and -d₂ for put (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
     *   <li>price
     *     <ol start="0">
     *       <li>symbol</li>
     *       <li>equation</li>
     *       <li>option parameters and values of d₁ and d₂ substituted in</li>
     *       <li>value</li>
     *     </ol>
     *   </li>
     * </ol>
     */
    @Override
    public CalculationModel priceCalculation() {
        double answer = this.price();
        String[] finalStep = this.finalCalculationStep(this.priceFormula(), answer);

        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(1),
                this.dCalculationStep(2),
                this.N_at_d_calculationStep(1, this.type == OptionType.CALL),
                this.N_at_d_calculationStep(2, this.type == OptionType.CALL),
                finalStep
            }, answer);
    }

    private String[] priceFormula() {
        String lhs = this.typeParameterLatex();
        String rhs = this.type == OptionType.CALL ? this.priceFormulaCallRhs() : this.priceFormulaPutRhs();

        return new String[] { lhs.trim(), rhs };
    }

    private String priceFormulaCallRhs() {
        return LATEX_S.trim() + this.dividendDiscountFactorLatex() + standardNormalCdfLatex(this.dParameterLatex(1, true))
            + " - " + this.discountFactorLatex() + LATEX_K + standardNormalCdfLatex(this.dParameterLatex(2, true));
    }

    private String priceFormulaPutRhs() {
        return this.discountFactorLatex().trim() + LATEX_K + standardNormalCdfLatex(this.dParameterLatex(2, false))
            + " - " + LATEX_S + this.dividendDiscountFactorLatex() + standardNormalCdfLatex(this.dParameterLatex(1, false));
    }

    //----------------------------------------------------------------------
    //endregion price

    //region delta
    //----------------------------------------------------------------------

    @Override
    public double delta() {
        return this.C̟P̠ * Math.exp(-this.q * this.τ) * this.N(this.C̟P̠ * this.d(1));
    }

    /**
     * Returns the details of the delta (Δ) calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal CDF at +d₁ for call and -d₁ for put (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
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
        double answer = this.delta();
        String[] finalStep = this.finalCalculationStep(this.deltaFormula(), answer);

        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(1),
                this.N_at_d_calculationStep(1, this.type == OptionType.CALL),
                finalStep
            }, answer);
    }

    private String[] deltaFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterLatex(), LATEX_S);

        String rhs = (this.type == OptionType.CALL ? "" : "-")
            + this.dividendDiscountFactorLatex().trim()
            + standardNormalCdfLatex(this.dParameterLatex(1, this.type == OptionType.CALL));

        return new String[] { LATEX_Δ.trim(), lhs, rhs };
    }

    //----------------------------------------------------------------------
    //endregion delta

    //region gamma
    //----------------------------------------------------------------------

    @Override
    public double gamma() {
        return Math.exp(-this.q * this.τ) * this.N̕(this.d(1)) / (this.S * this.σ * Math.sqrt(this.τ));
    }

    /**
     * Returns the details of the gamma (Γ) calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal PDF at d₁ (see {@link AbstractAnalyticalOption#standardNormalPdfCalculationStep(String, double)})</li>
     *   <li>gamma (Γ)
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
    public CalculationModel gammaCalculation() {
        double answer = this.gamma();
        String[] finalStep = this.finalCalculationStep(this.gammaFormula(), answer);

        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(1),
                this.N̕_at_d_calculationStep(1, true),
                finalStep
            }, answer);
    }

    private String[] gammaFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterLatex(), LATEX_S, "2");

        String rhs = this.dividendDiscountFactorLatex().trim()
            + LatexUtils.fraction(
                standardNormalPdfLatex(this.dParameterLatex(1, true)),
                LATEX_S + LATEX_σ + LatexUtils.squareRoot(LATEX_τ)
            );

        return new String[] { LATEX_Γ.trim(), lhs, rhs };
    }

    //----------------------------------------------------------------------
    //endregion gamma

    //region vega
    //----------------------------------------------------------------------

    @Override
    public double vega() {
        return this.S * Math.exp(-this.q * this.τ) * this.N̕(this.d(1)) * Math.sqrt(this.τ);
    }

    /**
     * Returns the details of the vega calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal PDF at d₁ (see {@link AbstractAnalyticalOption#standardNormalPdfCalculationStep(String, double)})</li>
     *   <li>vega
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
    public CalculationModel vegaCalculation() {
        double answer = this.vega();
        String[] finalStep = this.finalCalculationStep(this.vegaFormula(), answer);

        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(1),
                this.N̕_at_d_calculationStep(1, true),
                finalStep
            }, answer);
    }

    private String[] vegaFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterLatex(), LATEX_σ);

        String rhs = LATEX_S.trim()
            + this.dividendDiscountFactorLatex()
            + standardNormalPdfLatex(this.dParameterLatex(1, true))
            + LatexUtils.squareRoot(LATEX_τ);

        return new String[] { LATEX_VEGA.trim(), lhs, rhs };
    }

    //----------------------------------------------------------------------
    //endregion vega

    //region theta
    //----------------------------------------------------------------------

    @Override
    public double theta() {
        double term1 = -Math.exp(-this.q * this.τ) * (this.S * this.N̕(this.d(1)) * this.σ) / (2d * Math.sqrt(this.τ));
        double term2 = this.r * this.K * Math.exp(-this.r * this.τ) * this.N(this.C̟P̠ * this.d(2));
        double term3 = this.q * this.S * Math.exp(-this.q * this.τ) * this.N(this.C̟P̠ * this.d(1));
        return term1 + this.C̠P̟ *  term2 + this.C̟P̠ * term3;
    }

    /**
     * Returns the details of the theta (Θ) calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁ (see {@link #dCalculationStep(int)})</li>
     *   <li>d₂ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal CDF at +d₁ for call and -d₁ for put (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
     *   <li>standard normal CDF at +d₂ for call and -d₂ for put (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
     *   <li>standard normal PDF at d₁ (see {@link AbstractAnalyticalOption#standardNormalPdfCalculationStep(String, double)})</li>
     *   <li>theta (Θ)
     *     <ol start="0">
     *       <li>symbol</li>
     *       <li>equation</li>
     *       <li>option parameters and values of d₁ and d₂ substituted in</li>
     *       <li>value</li>
     *     </ol>
     *   </li>
     * </ol>
     */
    @Override
    public CalculationModel thetaCalculation() {
        double answer = this.theta();
        String[] finalStep = this.finalCalculationStep(this.thetaFormula(), answer);

        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(1),
                this.dCalculationStep(2),
                this.N_at_d_calculationStep(1, this.type == OptionType.CALL),
                this.N_at_d_calculationStep(2, this.type == OptionType.CALL),
                this.N̕_at_d_calculationStep(1, true),
                finalStep
            }, answer);
    }

    private String[] thetaFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterLatex(), LATEX_τ);

        // RHS
        String rhsTerm1 = "- " + this.dividendDiscountFactorLatex()
            + LatexUtils.fraction(
                LATEX_S + standardNormalPdfLatex(this.dParameterLatex(1, true)) + LATEX_σ,
                "2 " + LatexUtils.squareRoot(LATEX_τ)
            );

        String rhsTerm2 = (this.type == OptionType.CALL ? " - " : " + ")
            + LATEX_r + LATEX_K + this.discountFactorLatex()
            + standardNormalCdfLatex(this.dParameterLatex(2, this.type == OptionType.CALL));

        String rhsTerm3 = (this.type == OptionType.CALL ? " + " : " - ")
            + LATEX_q + LATEX_S + this.dividendDiscountFactorLatex()
            + standardNormalCdfLatex(this.dParameterLatex(1, this.type == OptionType.CALL));

        String rhs = rhsTerm1 + rhsTerm2 + rhsTerm3;

        return new String[] { LATEX_Θ.trim(), lhs, rhs };
    }

    //----------------------------------------------------------------------
    //endregion theta

    //region rho
    //----------------------------------------------------------------------

    @Override
    public double rho() {
        return this.C̟P̠ * this.K * this.τ * Math.exp(-this.r * this.τ) * this.N(this.C̟P̠ * this.d(2));
    }

    /**
     * Returns the details of the rho (ρ) calculation for a European option.
     * <p>For a list of parameters used in the calculation see {@link #optionParameters}.</p>
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₂ (see {@link #dCalculationStep(int)})</li>
     *   <li>standard normal CDF at +d₂ for call and -d₂ for put (see {@link AbstractAnalyticalOption#standardNormalCdfCalculationStep(String, double)})</li>
     *   <li>rho (ρ)
     *     <ol start="0">
     *       <li>symbol</li>
     *       <li>PDE</li>
     *       <li>equation</li>
     *       <li>option parameters and value of d₂ substituted in</li>
     *       <li>value</li>
     *     </ol>
     *   </li>
     * </ol>
     */
    @Override
    public CalculationModel rhoCalculation() {
        double answer = this.rho();
        String[] finalStep = this.finalCalculationStep(this.rhoFormula(), answer);

        return new CalculationModel(
            new String[][] {
                this.dCalculationStep(2),
                this.N_at_d_calculationStep(2, this.type == OptionType.CALL),
                finalStep
            }, answer);
    }

    private String[] rhoFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterLatex(), LATEX_r);

        String rhs = (this.type == OptionType.CALL ? "" : "-")
            + LATEX_K + LATEX_τ + this.discountFactorLatex()
            + standardNormalCdfLatex(this.dParameterLatex(2, this.type == OptionType.CALL));

        return new String[] { LATEX_ρ.trim(), lhs.trim(), rhs };
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
    @Override
    public Map<String, String> optionParameters() {
        return this.baseOptionParameters();
    }

    //region private methods
    //----------------------------------------------------------------------

    private String[] N_at_d_calculationStep(int i, boolean positive) {
        return this.standardNormalCdfCalculationStep(this.dParameterLatex(i, positive), (positive ? 1d : -1d) * this.d(i));
    }

    private String[] N̕_at_d_calculationStep(int i, boolean positive) {
        return this.standardNormalPdfCalculationStep(this.dParameterLatex(i, positive), (positive ? 1d : -1d) * this.d(i));
    }

    private String[] finalCalculationStep(String[] formula, double answer) {
        EquationInput[] inputs = Stream.concat(
            Arrays.stream(this.baseCalculationInputs(LatexDelimeterType.PARENTHESIS)),
            Arrays.stream(this.dEquationSubstitutionValues())
        ).toArray(EquationInput[]::new);

        return FormulaUtils.solve(formula, inputs, this.roundCalculationStepValue(answer));
    }

    //----------------------------------------------------------------------
    //endregion private methods
}
