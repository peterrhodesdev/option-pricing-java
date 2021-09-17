package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.core.AbstractOption;
import dev.peterrhodes.optionpricing.core.Parameter;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.List;

/**
 * A fully customizable option that supports every available setting.
 */
public class ExoticOption extends AbstractOption {

    // TODO create list of parameters that gets updated depending on the configuration of the option.

    /**
     * Creates a configurable/exotic option with the specified parameters.
     *
     * @param type {@link AbstractOption#type}
     * @param S {@link AbstractOption#S}
     * @param K {@link AbstractOption#K}
     * @param T {@link AbstractOption#T}
     * @param vol {@link AbstractOption#vol}
     * @param r {@link AbstractOption#r}
     * @param q {@link AbstractOption#q}
     * @throws IllegalArgumentException from {@link AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     */
    public ExoticOption(OptionStyle style, OptionType type, double S, double K, double T, double vol, double r, double q) throws IllegalArgumentException {
        super(style, type, S, K, T, vol, r, q);
    }

    /**
     * Returns a list of the parameters/variables used to define the option.
     *
     * @return option parameters list
     * <ol start="0">
     *   <li>call or put</li>
     *   <li>spot price</li>
     *   <li>exercise price</li>
     *   <li>time to maturity</li>
     *   <li>volatility</li>
     *   <li>risk-free rate</li>
     *   <li>dividend yield</li>
     * </ol>
     */
    public List<Parameter> optionParameters() {
        return this.baseParameters();
    }
}
