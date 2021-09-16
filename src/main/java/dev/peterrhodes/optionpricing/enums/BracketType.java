package dev.peterrhodes.optionpricing.enums;

/**
 * Bracket types that can be used in LaTex equations.
 */
public enum BracketType {

    /**
     * Brackets not allowed.
     */
    NONE,

    /**
     * Round brackets (parentheses), e.g. (x + y).
     */
    ROUND,

    /**
     * Square brackets, e.g. [x + y].
     */
    SQUARE,

    /**
     * Curly brackets, e.g. {x + y}.
     */
    CURLY
}
