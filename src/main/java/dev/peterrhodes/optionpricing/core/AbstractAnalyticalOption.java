package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.enums.PrecisionType;
import dev.peterrhodes.optionpricing.utils.FormulaUtils;
import dev.peterrhodes.optionpricing.utils.LatexUtils;
import dev.peterrhodes.optionpricing.utils.NumberUtils;
import lombok.NonNull;
import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Base class for concrete option classes that have an analytical solution, e.g.&nbsp;vanilla European options.&nbsp;If the specific option doesn't have an analytical solution then it should extend {@link AbstractOption}.
 */
public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {
    
    protected Integer calculationStepPrecisionDigits;
    protected PrecisionType calculationStepPrecisionType;
    private NormalDistribution normalDistribution;

    /**
     * Creates an abstract analytical option.&nbsp;For a description of the arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) throws IllegalArgumentException, NullPointerException {
        super(style, type, spotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
        this.calculationStepPrecisionDigits = 3;
        this.calculationStepPrecisionType = PrecisionType.SIGNIFICANT_FIGURES;
        this.normalDistribution = new NormalDistribution();
    }

    //region standard normal
    //----------------------------------------------------------------------

    /**
     * Returns the standard normal cumulative distribution function (CDF) evaluated at {@code x}.
     *
     * @param x point to evaluate the standard normal CDF at
     * @return standard normal CDF at {@code x}
     */
    public double standardNormalCdf(double x) {
        return this.normalDistribution.cumulativeProbability(x);
    }

    /**
     * Returns the standard normal probability density function (PDF) evaluated at {@code x}.
     *
     * @param x point to evaluate the standard normal PDF at
     * @return standard normal PDF at {@code x}
     */
    public double standardNormalPdf(double x) {
        return this.normalDistribution.density(x);
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
    public String[] standardNormalCdfCalculationStep(String variableLatex, Number variableValue) {
        String formula = standardNormalCdfLatex(variableLatex).trim();
        double answer = this.standardNormalCdf(variableValue.doubleValue());
        EquationInput input = new EquationInput.Builder(variableLatex)
            .withNumberValue(variableValue)
            .withPrecision(this.calculationStepPrecisionDigits, this.calculationStepPrecisionType)
            .build();

        return FormulaUtils.solve(
            new String[] { formula },
            new EquationInput[] { input },
            this.roundCalculationStepValue(answer)
        );
    }

    /**
     * Returns the details of a standard normal probability density function calculation step.
     * <p>The parts of the calculation step are:</p>
     * <ol start="0">
     *   <li>function notation</li>
     *   <li>function with the value of the variable substituted in</li>
     *   <li>calculation result</li>
     * </ol>
     */
    public String[] standardNormalPdfCalculationStep(String variableLatex, Number variableValue) {
        String formula = standardNormalPdfLatex(variableLatex).trim();
        double answer = this.standardNormalPdf(variableValue.doubleValue());
        EquationInput input = new EquationInput.Builder(variableLatex)
            .withNumberValue(variableValue)
            .withPrecision(this.calculationStepPrecisionDigits, this.calculationStepPrecisionType)
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
     * Set the precision of the calculated values (not option parameters) for display in the LaTeX mathematical expressions.
     * <p>The default values are:</p>
     * <ul>
     *   <li>{@code precisionDigits}: 3</li>
     *   <li>{@code precisionType}: {@link PrecisionType#SIGNIFICANT_FIGURES}</li>
     * </ul>
     *
     * @param precisionDigits number of digits of precision
     * @param precisionType type of precision for formatting
     * @throws NullPointerException if {@code precisionType} is null
     * @throws IllegalArgumentException if {@code precisionDigits} is less than zero
     */
    @Override
    public final void setCalculationStepPrecision(int precisionDigits, @NonNull PrecisionType precisionType) throws NullPointerException {
        if (precisionDigits < 0) {
            throw new IllegalArgumentException("precisionDigits must be greater than or equal to zero");
        }
        this.calculationStepPrecisionDigits = precisionDigits;
        this.calculationStepPrecisionType = precisionType;
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
        return " " + LatexUtils.exponential("-" + LATEX_r + LATEX_τ) + " ";
    }

    protected final String dividendDiscountFactorLatex() {
        return " " + LatexUtils.exponential("-" + LATEX_q + LATEX_τ) + " ";
    }

    /**
     * Standard normal cumulative distribution function, usually denoted by the capital Greek letter phi 𝚽 .
     */
    protected double N(double x) {
        return this.standardNormalCdf(x);
    }

    /**
     * Standard normal probability density function, usually denoted with the small Greek letter phi 𝜑.
     * Note: Using Nʹinstead of N̕ is more readable but causes an error in PMD, and don't want to exclude the files from checking.
     * TODO: Raise issue with PMD
     */
    protected double N̕(double x) {
        return this.standardNormalPdf(x);
    }

    protected final String roundCalculationStepValue(double value) {
        return NumberUtils.precision(value, this.calculationStepPrecisionDigits, this.calculationStepPrecisionType);
    }

    protected static final String standardNormalCdfLatex(String argument) {
        return " \\mathrm{N} ( " + argument + " ) ";
    }

    protected static final String standardNormalPdfLatex(String argument) {
        return " \\mathrm{N'} ( " + argument + " ) ";
    }

    //region constants
    //----------------------------------------------------------------------

    protected static final String LATEX_Δ = " \\Delta ";
    protected static final String LATEX_Γ = " \\Gamma ";
    protected static final String LATEX_VEGA = " \\mathcal {V} ";
    protected static final String LATEX_ρ = " \\rho ";
    protected static final String LATEX_Θ = " \\Theta ";

    //----------------------------------------------------------------------
    //endregion constants
}
