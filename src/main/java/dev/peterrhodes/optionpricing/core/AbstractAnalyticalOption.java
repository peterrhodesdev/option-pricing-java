package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import dev.peterrhodes.optionpricing.utils.NumberUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for concrete option classes that have an analytical solution, e.g.&nbsp;vanilla European options.&nbsp;If the specific option doesn't have an analytical solution then it should extend {@link AbstractOption}.
 */
public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {

    protected Integer calculationStepPrecision = 3;
    protected RoundingMethod calculationStepRoundingMethod = RoundingMethod.SIGNIFICANT_FIGURES;

    /**
     * Creates an abstract analytical option with the specified parameters.
     *
     * @param style {@link AbstractOption#style}
     * @param type {@link AbstractOption#type}
     * @param S {@link AbstractOption#S}
     * @param K {@link AbstractOption#K}
     * @param T {@link AbstractOption#T}
     * @param vol {@link AbstractOption#vol}
     * @param r {@link AbstractOption#r}
     * @param q {@link AbstractOption#q}
     * @throws NullPointerException from {@link AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     * @throws IllegalArgumentException from {@link AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, Number S, Number K, Number T, Number vol, Number r, Number q) throws IllegalArgumentException, NullPointerException {
        super(style, type, S, K, T, vol, r, q);
    }

    protected final EquationInput[] baseCalculationInputs(LatexDelimeterType latexDelimeterType) {
        return new EquationInput[] {
            new EquationInput.Builder(NOTATION_S.trim()).withNumberValue(this.S).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(NOTATION_K.trim()).withNumberValue(this.K).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(NOTATION_T.trim()).withNumberValue(this.T).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(NOTATION_VOL.trim()).withNumberValue(this.vol).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(NOTATION_R.trim()).withNumberValue(this.r).withDelimeter(latexDelimeterType).build(),
            new EquationInput.Builder(NOTATION_Q.trim()).withNumberValue(this.q).withDelimeter(latexDelimeterType).build()
        };
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
