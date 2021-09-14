package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;

/**
 * Base class for concrete option classes that have an analytical solution, e.g. vanilla European options.
 * If the specific option doesn't have an analytical solution then it should extend {@link AbstractOption}.
 */
public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {

    /**
     * Creates an abstract analytical option with the specified parameters.
     *
     * @see AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, double S, double K, double T, double vol, double r, double q) throws IllegalArgumentException {
        super(style, type, S, K, T, vol, r, q);
    }
}
