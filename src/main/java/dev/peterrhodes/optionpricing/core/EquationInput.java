package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.BracketType;
import lombok.Getter;

/**
 * Holds the details of an input that is to be substituted into a mathemtical equation.
 */
@Getter
public class EquationInput {

    /**
     * Key for identifying the input variable name in an equation.
     */
    private String key;

    /**
     * The value to be substituted into the equation for the {@link #key}.
     */
    private String value;

    /**
     * The type of brackets to surround the value with when it's substituted into the equation.
     */
    private BracketType bracketType;

    /**
     * Creates an object that represents an input to a mathematical equation.
     *
     * @param key Key for identifying the input variable name in an equation.
     * @param value The value to be substituted into the equation for the {@link #key}.
     * @param bracketType The type of brackets to surround the value with when it's substituted into the equation.
     */
    public EquationInput(String key, String value, BracketType bracketType) {
        this.key = key;
        this.value = value;
        this.bracketType = bracketType;
    }

    /**
     * {@code bracketType} defaults to {@link BracketType#NONE}.
     *
     * @see #EquationInput(String, String, BracketType)
     */
    public EquationInput(String key, String value) {
        this(key, value, BracketType.NONE);
    }
}
