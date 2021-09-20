package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import dev.peterrhodes.optionpricing.utils.FormulaUtils;
import dev.peterrhodes.optionpricing.utils.LatexUtils;
import dev.peterrhodes.optionpricing.utils.NumberUtils;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Base class for concrete option classes that have an analytical solution, e.g.&nbsp;vanilla European options.&nbsp;If the specific option doesn't have an analytical solution then it should extend {@link AbstractOption}.
 */
public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {
    
    protected NormalDistribution N;
    protected Integer calculationStepPrecision;
    protected RoundingMethod calculationStepRoundingMethod;

    /**
     * Creates an abstract analytical option.&nbsp;For a description of the arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) throws IllegalArgumentException, NullPointerException {
        super(style, type, spotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
        this.calculationStepPrecision = 3;
        this.calculationStepRoundingMethod = RoundingMethod.SIGNIFICANT_FIGURES;
        this.N = new NormalDistribution();
    }

    //region standard normal
    //----------------------------------------------------------------------

    /**
     * TODO.
     */
    public double standardNormalCdf(double x) {
        return this.N.cumulativeProbability(x);
    }

    // Use this in calculations to simplify and resemble mth notation
    protected double N_at(double x) {
        return this.standardNormalCdf(x);
    }

    /**
     * Returns the details of a standard normal cumulative distribution function calculation step.
     * <p>The parts of the calculation step are:</p>
     * <ol start="0">
     *   <li>function notation</li>
     *   <li>function with the value of the variable substituted in</li>
     *   <li>calculation result</li>
     * </ol>
     */
    public String[] standardNormalCdfCalculationStep(String variableLatex, double variableValue) {
        String formula = standardNormalCdfLatex(variableLatex).trim();
        double answer = this.standardNormalCdf(variableValue);
        EquationInput input = new EquationInput.Builder(variableLatex)
            .withNumberValue(variableValue)
            .withPrecision(this.calculationStepPrecision, this.calculationStepRoundingMethod)
            .build();

        return FormulaUtils.solve(
            new String[] { formula },
            new EquationInput[] { input },
            this.roundCalculationStepValue(answer)
        );
    }

    //----------------------------------------------------------------------
    //endregion standard normal

    /**
     * TODO.
     */
    public final void setCalculationStepPrecision(int precision, RoundingMethod roundingMethod) {
        this.calculationStepPrecision = precision;
        this.calculationStepRoundingMethod = roundingMethod;
    }

    protected final EquationInput[] baseCalculationInputs(LatexDelimeterType latexDelimeterType) {
        return new EquationInput[] {
            new EquationInput.Builder(LATEX_S.trim()).withNumberValue(this.spotPrice).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(LATEX_K.trim()).withNumberValue(this.strikePrice).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(LATEX_τ.trim()).withNumberValue(this.timeToMaturity).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(LATEX_σ.trim()).withNumberValue(this.volatility).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(LATEX_r.trim()).withNumberValue(this.riskFreeRate).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(LATEX_q.trim()).withNumberValue(this.dividendYield).withDelimeter(latexDelimeterType).build()
        };
    }

    protected final String discountFactorLatex() {
        return LatexUtils.exponential("-" + LATEX_r + LATEX_τ);
    }

    protected final String dividendDiscountFactorLatex() {
        return LatexUtils.exponential("-" + LATEX_q + LATEX_τ);
    }

    protected final String roundCalculationStepValue(double value) {
        return NumberUtils.round(value, this.calculationStepPrecision, this.calculationStepRoundingMethod);
    }

    protected static final String standardNormalCdfLatex(String argument) {
        return " \\mathrm{N} ( " + argument + " ) ";
    }

    //region constants
    //----------------------------------------------------------------------

    protected static final String LATEX_Δ = " \\Delta ";

    //----------------------------------------------------------------------
    //endregion constants
}
