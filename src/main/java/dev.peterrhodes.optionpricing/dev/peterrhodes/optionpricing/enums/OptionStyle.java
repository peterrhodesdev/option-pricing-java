package dev.peterrhodes.optionpricing.enums;

/**
 * Option styles available for calculation.&nbsp;An option's style is usually defined by the exercise rights.
 */
public enum OptionStyle {

    /**
     * European option: can only be exercised at maturity (the option's expiration date).
     */
    EUROPEAN,

    /**
     * American option: can be exercised at any time up to and including the expiration date.
     */
    AMERICAN
}
