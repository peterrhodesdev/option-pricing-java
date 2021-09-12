package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionType;

public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {

    /**
     * Creates an abstract analytical option with the specified parameters.
     * @see AbstractOption#AbstractOption()
     */
    public AbstractAnalyticalOption(OptionType optionType, double S, double K, double T, double v, double r, double q) throws IllegalArgumentException {
        super(optionType, S, K, T, v, r, q);
    }
}
