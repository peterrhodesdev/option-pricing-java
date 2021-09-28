package dev.peterrhodes.optionpricing.internal.analyticoptions;

import dev.peterrhodes.optionpricing.AnalyticOption;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.internal.OptionImpl;
import dev.peterrhodes.optionpricing.internal.common.EquationInput;
import dev.peterrhodes.optionpricing.internal.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.internal.enums.PrecisionType;
import dev.peterrhodes.optionpricing.internal.utils.FormulaUtils;
import dev.peterrhodes.optionpricing.internal.utils.LatexUtils;
import dev.peterrhodes.optionpricing.internal.utils.MathUtils;
import dev.peterrhodes.optionpricing.internal.utils.NumberUtils;
import dev.peterrhodes.optionpricing.internal.utils.ValidationUtils;

/**
 * Base class for concrete option classes that have an analytical solution, e.g.&nbsp;vanilla European options.
 */
public abstract class AbstractAnalyticOption extends OptionImpl implements AnalyticOption {
    
    protected Integer calculationStepPrecisionDigits;
    protected PrecisionType calculationStepPrecisionType;

    // Math notation
    protected boolean isCall;
    protected double S; // initial spot price
    protected double K; // strike price
    protected double τ; // time to maturity
    protected double σ; // volatility
    protected double r; // risk free rate
    protected double q; // dividend yield
    protected double C̟P̠; // +1 for call, -1 for put
    protected double C̠P̟; // -1 for call, +1 for put

    /**
     * Creates the base class for an analytic option.
     */
    public AbstractAnalyticOption(OptionStyle optionStyle, OptionType optionType, Number initialSpotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) {
        super(initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
        this.setOptionStyle(optionStyle);
        this.setOptionType(optionType);

        // defaults
        this.calculationStepPrecisionDigits = 3;
        this.calculationStepPrecisionType = PrecisionType.SIGNIFICANT_FIGURES;

        // math notation
        this.isCall = optionType == OptionType.CALL;
        this.S = initialSpotPrice.doubleValue();
        this.K = strikePrice.doubleValue();
        this.τ = timeToMaturity.doubleValue();
        this.σ = volatility.doubleValue();
        this.r = riskFreeRate.doubleValue();
        this.q = dividendYield.doubleValue();
        this.C̟P̠ = optionType == OptionType.CALL ? 1d : -1d;
        this.C̠P̟ = this.C̟P̠ * -1d;
    }

    @Override
    public final void setCalculationStepPrecision(int precisionDigits, PrecisionType precisionType) throws NullPointerException, IllegalArgumentException {
        ValidationUtils.checkNotNull(precisionType, "precisionType");
        //ValidationUtils.checkGreaterThanZero(precisionDigits, "precisionDigits");
        if (precisionDigits < 0) {
            throw new IllegalArgumentException("precisionDigits must be greater than or equal to zero");
        }
        this.calculationStepPrecisionDigits = precisionDigits;
        this.calculationStepPrecisionType = precisionType;
    }

    //region standard normal
    //----------------------------------------------------------------------

    /**
     * Returns the details of a standard normal cumulative distribution function calculation step.
     * <p>The parts of the calculation step are:</p>
     * <ol start="0">
     *   <li>function notation</li>
     *   <li>function with the value of the variable substituted in</li>
     *   <li>calculation result</li>
     * </ol>
     */
    public final String[] standardNormalCdfCalculationStep(String variableLatex, Number variableValue) {
        String formula = standardNormalCdfLatex(variableLatex).trim();
        double answer = this.N(variableValue.doubleValue());
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
    public final String[] standardNormalPdfCalculationStep(String variableLatex, Number variableValue) {
        String formula = standardNormalPdfLatex(variableLatex).trim();
        double answer = this.N̕(variableValue.doubleValue());
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

    protected final EquationInput[] baseCalculationInputs(LatexDelimeterType latexDelimeterType) {
        return new EquationInput[] {
            this.baseCalculationInput(LATEX_S.trim(), this.initialSpotPrice(), latexDelimeterType),
            this.baseCalculationInput(LATEX_K.trim(), this.strikePrice(), latexDelimeterType),
            this.baseCalculationInput(LATEX_τ.trim(), this.timeToMaturity(), latexDelimeterType),
            this.baseCalculationInput(LATEX_σ.trim(), this.volatility(), latexDelimeterType),
            this.baseCalculationInput(LATEX_r.trim(), this.riskFreeRate(), latexDelimeterType),
            this.baseCalculationInput(LATEX_q.trim(), this.dividendYield(), latexDelimeterType),
        };
    }

    protected final EquationInput baseCalculationInput(String notation, Number value, LatexDelimeterType latexDelimeterType) {
        return new EquationInput.Builder(notation)
                .withNumberValue(value)
                .withDelimeter(latexDelimeterType)
                .build();
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
    protected final double N(double x) {
        return MathUtils.standardNormalCdf(x);
    }

    /**
     * Standard normal probability density function, usually denoted with the small Greek letter phi 𝜑.
     * Note: Using Nʹinstead of N̕ is more readable but causes an error in PMD, and don't want to exclude the files from checking.
     * TODO: Raise issue with PMD
     */
    protected final double N̕(double x) {
        return MathUtils.standardNormalPdf(x);
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

    /**
     * Returns "C" for a call option, "P" for a put option.
     *
     * @return type parameter
     */
    protected final String typeParameterLatex() {
        return this.isCall ? " C " : " P ";
    }

    //region constants
    //----------------------------------------------------------------------
    
    // Base option parameters
    protected static final String LATEX_S = " S ";
    protected static final String LATEX_K = " K ";
    protected static final String LATEX_τ = " \\tau ";
    protected static final String LATEX_σ = " \\sigma ";
    protected static final String LATEX_r = " r ";
    protected static final String LATEX_q = " q ";

    // Greeks
    protected static final String LATEX_Δ = " \\Delta ";
    protected static final String LATEX_Γ = " \\Gamma ";
    protected static final String LATEX_VEGA = " \\mathcal {V} ";
    protected static final String LATEX_ρ = " \\rho ";
    protected static final String LATEX_Θ = " \\Theta ";

    //----------------------------------------------------------------------
    //endregion constants
}
