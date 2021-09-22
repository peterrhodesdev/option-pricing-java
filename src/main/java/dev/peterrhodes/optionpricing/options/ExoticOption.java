package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.Map;

/**
 * A customizable option.
 */
public final class ExoticOption extends AbstractOption {

    private ExoticOption(Builder builder) {
        super(builder.style, builder.type, builder.spotPrice, builder.strikePrice, builder.timeToMaturity, builder.volatility, builder.riskFreeRate, builder.dividendYield);
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

    /**
     * Builder class for {@link ExoticOption}.
     */
    public static class Builder {
        private OptionStyle style;
        private OptionType type;
        private Number spotPrice;
        private Number strikePrice;
        private Number timeToMaturity;
        private Number volatility;
        private Number riskFreeRate;
        private Number dividendYield;

        /**
         * TODO.
         */
        public Builder(OptionType type, Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) {
            this.type = type;
            this.spotPrice = spotPrice;
            this.strikePrice = strikePrice;
            this.timeToMaturity = timeToMaturity;
            this.volatility = volatility;
            this.riskFreeRate = riskFreeRate;
            this.dividendYield = dividendYield;
        }

        /**
         * TODO.
         */
        public Builder styleAmerican() {
            this.style = OptionStyle.AMERICAN;
            return this;
        }

        /**
         * TODO.
         */
        public Builder styleEuropean() {
            this.style = OptionStyle.EUROPEAN;
            return this;
        }

        /**
         * TODO.
         */
        public ExoticOption build() {
            return new ExoticOption(this);
        }
    }
}
