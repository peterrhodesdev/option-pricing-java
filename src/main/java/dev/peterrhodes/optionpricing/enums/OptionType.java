package dev.peterrhodes.optionpricing.enums;

/**
 * Option types that can be used.
 * An option's type is based on its rights, i.e. it's either a call or a put option.
 */
public enum OptionType {

    /**
     * Call option: the holder has the right, but not the obligation, to buy an asset.
     */
    CALL,

    /**
     * Put option: the holder has the right, but not the obligation, to sell an asset.
     */
    PUT
}
