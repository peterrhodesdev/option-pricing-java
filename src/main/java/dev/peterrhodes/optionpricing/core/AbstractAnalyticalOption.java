package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;

public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {

    /**
     * Creates an abstract analytical option with the specified parameters.
     * @see AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, double S, double K, double T, double v, double r, double q) throws IllegalArgumentException {
        super(style, type, S, K, T, v, r, q);
    }
}
