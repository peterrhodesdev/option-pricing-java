package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import lombok.Getter;

/**
 * Holds the details of an input that is to be substituted into a mathemtical equation.
 */
@Getter
public class EquationInput implements Cloneable {

    /**
     * Key for identifying the input variable name in an equation.
     */
    private String key;

    /**
     * The value to be substituted into the equation for the {@link #key}.
     */
    private String value;

    /**
     * The type of delimeter to surround the value with when it's substituted into the equation.
     */
    private LatexDelimeterType latexDelimeterType;

    /**
     * Creates an object that represents an input to a mathematical equation.
     *
     * @param key Key for identifying the input variable name in an equation.
     * @param value The value to be substituted into the equation for the {@link #key}.
     * @param latexDelimeterType The type of delimeter to surround the value with when it's substituted into the equation.
     */
    public EquationInput(String key, String value, LatexDelimeterType latexDelimeterType) {
        this.key = key;
        this.value = value;
        this.latexDelimeterType = latexDelimeterType;
    }

    /**
     * {@code latexDelimeterType} defaults to {@link LatexDelimeterType#NONE}.
     *
     * @see #EquationInput(String, String, LatexDelimeterType)
     */
    public EquationInput(String key, String value) {
        this(key, value, LatexDelimeterType.NONE);
    }

    /**
     * Clone the object.
     *
     * @return the cloned object
     */
    @Override
    public Object clone() {
        try {
            return (EquationInput) super.clone();
        } catch (CloneNotSupportedException e) {
            return new EquationInput(this.key, this.value, this.latexDelimeterType);
        }
    }
}
