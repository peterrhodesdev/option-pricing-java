package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.core.AbstractOption;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;

public class ExoticOption extends AbstractOption {
    
    /**
     * Creates a configurable/exotic option with the specified parameters.
     * @see AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)
     */
    public ExoticOption(OptionStyle style, OptionType type, double S, double K, double T, double v, double r, double q) throws IllegalArgumentException {
        super(style, type, S, K, T, v, r, q);
    }
}
