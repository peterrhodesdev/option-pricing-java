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
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
     * @throws NullPointerException from {@link AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     * @throws IllegalArgumentException from {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     */
    public EuropeanOption(OptionType type, Number S, Number K, Number T, Number vol, Number r, Number q) throws IllegalArgumentException, NullPointerException {
        super(OptionStyle.EUROPEAN, type, S, K, T, vol, r, q);
        this.N = new NormalDistribution();
    }

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
    public CalculationModel priceCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion price

    //region delta
    //----------------------------------------------------------------------

    @Override
    public double delta() {
        return this.typeFactor() * Math.exp(-this.q * this.T) * this.N_at_d(1, this.typeFactor());
    }

    /**
     * Calculation for the delta (Δ) of a European option.
     * <p>The calculation steps are:</p>
     * <ol start="0">
     *   <li>d₁</li>
     *   <li>call: N(d₁), put: N(-d₁)</li>
     *   <li>Δ</li>TODO describe array
     * </ol>
     */
    @Override
    public CalculationModel deltaCalculation() {
        String[] d1Step = this.dCalculationStep(1);
        String[] nd1Step = this.ndiCalculationStep(1, this.typeFactor());

        // delta
        double answer = this.delta();
        EquationInput[] inputs = Stream.concat(
                Arrays.stream(this.getBaseCalculationInputs(LatexDelimeterType.PARENTHESIS)),
                Arrays.stream(new EquationInput[] { this.dEquationSubstitutionValue(1) })
            )
            .toArray(EquationInput[]::new);
        String[] finalStep = FormulaUtils.solve(
            this.deltaFormula(),
            inputs,
            this.roundCalculationStepValue(answer)
        );

        return new CalculationModel(new String[][] { d1Step, nd1Step, finalStep }, answer);
    }

    /**
     * Formula for the delta (Δ) of a European option.&nbsp;For a list of parameters used in the formula see {@link #optionParameters}, and for a list of the functions see {@link #calculationFunctions}.
     * <p>The "where" components include the formulas for:</p>
     * <ol start="0">
     *   <li>d₁</li>
     * </ol>
     */
    private String[] deltaFormula() {
        String lhs = LatexUtils.partialDerivative(this.typeParameterNotation(), NOTATION_S);

        // RHS
        String factor = LatexUtils.exponential("-" + NOTATION_Q + NOTATION_T);
        String rhs = this.type == OptionType.CALL
            ? factor + notationStandardNormalCdf(this.dParameterNotation(1))
            : "-" + factor + notationStandardNormalCdf("-" + this.dParameterNotation(1));

        List<String> parts = Arrays.stream(new String[] { NOTATION_DELTA, lhs }).collect(Collectors.toList());
        if (this.type == OptionType.PUT) {
            parts.add(notationStandardNormalCdf(this.dParameterNotation(1)) + " - 1");
        }
        parts.add(rhs);
        
        return parts.toArray(String[]::new);
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
    public CalculationModel gammaCalculation() {
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
    public CalculationModel vegaCalculation() {
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
    public CalculationModel thetaCalculation() {
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
    public CalculationModel rhoCalculation() {
        throw new NotYetImplementedException();
    }

    //----------------------------------------------------------------------
    //endregion rho

    /**
     * Returns a list of the parameters/variables used to define the option.
     *
     * @return option parameters list
     * <ol start="0">
     *   <li>spot price</li>
     *   <li>exercise price</li>
     *   <li>time to maturity</li>
     *   <li>volatility</li>
     *   <li>risk-free rate</li>
     *   <li>dividend yield</li>
     * </ol>
     */
    public Map<String, String> getOptionParameters() {
        return this.getBaseParameters();
    }

    //======================================================================

    //region d₁, d₂
    //----------------------------------------------------------------------

    /**
     * Calculates the values of d₁ and d₂ in the Black-Scholes formula.
     *
     * @return dᵢ
     */
    private double d(int i) {
        return (Math.log(this.S / this.K) + (this.r - this.q + (i == 1 ? 1 : -1) * Math.pow(this.vol, 2) / 2) * this.T) / (this.vol * Math.sqrt(this.T));
    }

    private String[] dCalculationStep(int i) {
        String lhs = this.dParameterNotation(i);

        // RHS
        String iFactor = i == 1 ? " + " : " - ";
        String rhsNumerator = LatexUtils.naturalLogarithm(LatexUtils.fraction(NOTATION_S, NOTATION_K))
            + LatexUtils.subFormula(NOTATION_R + " - " + NOTATION_Q + iFactor + LatexUtils.half(LatexUtils.squared(NOTATION_VOL)), LatexDelimeterType.PARENTHESIS) + NOTATION_T;
        String rhsDenominator = LatexUtils.MATH_SYMBOL_GREEK_LETTER_SIGMA_LOWERCASE + LatexUtils.squareRoot(NOTATION_T);
        String rhs = LatexUtils.fraction(rhsNumerator, rhsDenominator);

        List<String> parts = new ArrayList();
        parts.add(lhs);
        if (i == 2) {
            parts.add(this.dParameterNotation(1) + " - " + NOTATION_VOL + LatexUtils.squareRoot(NOTATION_T));
        }
        parts.add(rhs);
        
        return FormulaUtils.solve(parts.toArray(String[]::new), this.getBaseCalculationInputs(LatexDelimeterType.NONE), this.roundCalculationStepValue(this.d(i)));
    }

    private String dParameterNotation(int i) {
        return String.format(" d_%d ", i);
    }
/*
    private EquationInput[] dEquationSubstitutionValues() {
        return new EquationInput[] {
            this.dEquationSubstitutionValue(1),
            this.dEquationSubstitutionValue(2)
        };
    }
*/

    private EquationInput dEquationSubstitutionValue(int i) {
        return new EquationInput.Builder(this.dParameterNotation(i).trim())
            .withNumberValue(this.d(i))
            .withPrecision(this.calculationStepPrecision, this.calculationStepRoundingMethod)
            .build();
    }

    //----------------------------------------------------------------------
    //endregion d₁, d₂

    //region N(dᵢ)
    //----------------------------------------------------------------------

    private double N_at_d(int i, double dFactor) {
        return this.N.cumulativeProbability(dFactor * this.d(i));
    }

    private String[] ndiCalculationStep(int i, double dFactor) {
        String formula = notationStandardNormalCdf((dFactor < 0 ? "-" : "") + this.dParameterNotation(i));
        double answer = this.N_at_d(i, dFactor);

        return FormulaUtils.solve(
            new String[] { formula },
            new EquationInput[] { this.dEquationSubstitutionValue(i) },
            this.roundCalculationStepValue(answer)
        );
    }

    //----------------------------------------------------------------------
    //endregion N(dᵢ)

    //region private methods
    //----------------------------------------------------------------------

    //----------------------------------------------------------------------
    //endregion private methods
}
