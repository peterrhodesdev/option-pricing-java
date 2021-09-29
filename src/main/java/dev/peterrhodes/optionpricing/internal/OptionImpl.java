package dev.peterrhodes.optionpricing.internal;

import dev.peterrhodes.optionpricing.Option;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.internal.common.PublicCloneable;

/**
 * Implementation of {@link Option}.
 */
public class OptionImpl implements Option, PublicCloneable<OptionImpl> {

    private OptionStyle optionStyle;
    private OptionType optionType;
    private Number initialSpotPrice;
    private Number strikePrice;
    private Number timeToMaturity;
    private Number volatility;
    private Number riskFreeRate;
    private Number dividendYield;

    /**
     * Creates an implementation of {@link Option}.
     */
    public OptionImpl(Number initialSpotPrice, Number strikePrice, Number timeToMaturity, Number volatility, Number riskFreeRate, Number dividendYield) {
        this.initialSpotPrice = initialSpotPrice;
        this.strikePrice = strikePrice;
        this.timeToMaturity = timeToMaturity;
        this.volatility = volatility;
        this.riskFreeRate = riskFreeRate;
        this.dividendYield = dividendYield;

        this.optionStyle = null;
        this.optionType = null;
    }

    @Override
    public OptionStyle optionStyle() {
        return this.optionStyle;
    }

    @Override
    public OptionType optionType() {
        return this.optionType;
    }

    @Override
    public Number initialSpotPrice() {
        return this.initialSpotPrice;
    }

    @Override
    public Number strikePrice() {
        return this.strikePrice;
    }

    @Override
    public Number timeToMaturity() {
        return this.timeToMaturity;
    }

    @Override
    public Number volatility() {
        return this.volatility;
    }

    @Override
    public Number riskFreeRate() {
        return this.riskFreeRate;
    }

    @Override
    public Number dividendYield() {
        return this.dividendYield;
    }

    //region setters
    //----------------------------------------------------------------------

    /**
     * Set optionStyle.
     */
    public void setOptionStyle(OptionStyle optionStyle) {
        this.optionStyle = optionStyle;
    }

    /**
     * Set optionType.
     */
    public void setOptionType(OptionType optionType) {
        this.optionType = optionType;
    }

    //----------------------------------------------------------------------
    //endregion setters

    /**
     * Clone the object.
     *
     * @return the cloned object
     */
    @Override
    public OptionImpl clone() {
        try {
            return (OptionImpl) super.clone();
        } catch (CloneNotSupportedException e) {
            OptionImpl optionImpl = new OptionImpl(initialSpotPrice, strikePrice, timeToMaturity, volatility, riskFreeRate, dividendYield);
            optionImpl.setOptionStyle(this.optionStyle);
            optionImpl.setOptionType(this.optionType);
            return optionImpl;
        }
    }
}
