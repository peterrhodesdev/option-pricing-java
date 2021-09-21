package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.core.AbstractOption;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.Map;

/**
 * A customizable option contract.
 */
public class ExoticOption extends AbstractOption {

    /**
     * Creates a configurable/exotic option.&nbsp;For a description of the arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.core.AbstractOption#AbstractOption(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    public ExoticOption(OptionStyle style, OptionType type, Number S, Number K, Number T, Number vol, Number r, Number q) throws IllegalArgumentException, NullPointerException {
        super(style, type, S, K, T, vol, r, q);
    }

    /**
     * Returns a map of the parameters/variables used to define the option.&nbsp;The key is the latex notation for the parameter and the value is its numeric value.
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
    public Map<String, String> optionParameters() {
        return this.baseOptionParameters();
    }
}
