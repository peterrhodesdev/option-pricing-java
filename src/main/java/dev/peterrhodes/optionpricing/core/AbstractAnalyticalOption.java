package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import dev.peterrhodes.optionpricing.utils.CopyUtils;
import dev.peterrhodes.optionpricing.utils.NumberUtils;

/**
 * Base class for concrete option classes that have an analytical solution, e.g.&nbsp;vanilla European options.&nbsp;If the specific option doesn't have an analytical solution then it should extend {@link AbstractOption}.
 */
public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {

    protected Integer calculationStepPrecision = 3;
    protected RoundingMethod calculationStepRoundingMethod = RoundingMethod.SIGNIFICANT_FIGURES;

    private EquationInput[] baseCalculationInputs;

    /**
     * Creates an abstract analytical option.&nbsp;For a description of the arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, Number S, Number K, Number T, Number vol, Number r, Number q) throws IllegalArgumentException, NullPointerException {
        super(style, type, S, K, T, vol, r, q);

        this.baseCalculationInputs = new EquationInput[] {
            new EquationInput.Builder(NOTATION_S.trim()).withNumberValue(S).build(),
            new EquationInput.Builder(NOTATION_K.trim()).withNumberValue(K).build(),
            new EquationInput.Builder(NOTATION_T.trim()).withNumberValue(T).build(),
            new EquationInput.Builder(NOTATION_VOL.trim()).withNumberValue(vol).build(),
            new EquationInput.Builder(NOTATION_R.trim()).withNumberValue(r).build(),
            new EquationInput.Builder(NOTATION_Q.trim()).withNumberValue(q).build()
        };
    }

    /**
     * TODO.
     */
    public final void setCalculationStepPrecision(int precision, RoundingMethod roundingMethod) {
        this.calculationStepPrecision = precision;
        this.calculationStepRoundingMethod = roundingMethod;
    }

    protected final EquationInput[] getBaseCalculationInputs(LatexDelimeterType latexDelimeterType) {
        EquationInput[] equationInputs = CopyUtils.deepCopy(this.baseCalculationInputs, EquationInput.class);
        for (EquationInput equationInput : equationInputs) {
            equationInput.setLatexDelimeterType(latexDelimeterType);
        }
        return equationInputs;
    }

    protected static final String notationStandardNormalCdf(String argument) {
        return " \\mathrm{N} (" + argument + ") ";
    }

    protected final String roundCalculationStepValue(double value) {
        return NumberUtils.round(value, this.calculationStepPrecision, this.calculationStepRoundingMethod);
    }

    //region constants
    //----------------------------------------------------------------------

    protected static final String NOTATION_DELTA = " \\Delta ";

    //----------------------------------------------------------------------
    //endregion constants
}
