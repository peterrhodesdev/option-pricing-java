package dev.peterrhodes.optionpricing.contracts;

import dev.peterrhodes.optionpricing.Contract;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;

/**
 * TODO.
 */
public final class ExoticContract extends AbstractContract {

    /**
     * Creates an exotic/customizable option contract.&nbsp;For a description of the other arguments and exceptions thrown see {@link dev.peterrhodes.optionpricing.common.AbstractContract#AbstractContract(OptionStyle, OptionType, Number, Number, Number, Number, Number, Number)}.
     */
    private ExoticContract(Builder builder) throws IllegalArgumentException, NullPointerException {
        //super(builder.style, builder.type, builder.S, builder.K, builder.τ, builder.σ, builder.r, builder.q);
        super(builder.style, builder.type, builder.spotPrice, builder.strikePrice, builder.timeToMaturity, builder.volatility, builder.riskFreeRate, builder.dividendYield);
    }

    /**
     * TODO.
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
        
        //public Builder(OptionType type, Number S, Number K, Number τ, Number σ, Number r, Number q) throws IllegalArgumentException, NullPointerException {
        /**
         * TODO.
         */
        public Builder(OptionType type, Number spotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) throws IllegalArgumentException, NullPointerException {
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
        public Builder americanStyle() {
            this.style = OptionStyle.AMERICAN;
            return this;
        }

        /**
         * TODO.
         */
        public Builder europeanStyle() {
            this.style = OptionStyle.EUROPEAN;
            return this;
        }

        /**
         * TODO.
         */
        public Contract build() {
            return new ExoticContract(this);
        }

    }
}
