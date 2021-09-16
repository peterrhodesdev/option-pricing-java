package dev.peterrhodes.optionpricing.core;

import lombok.Getter;

/**
 * Represents a parameter in a formula.
 */
@Getter
public class Parameter implements Cloneable {

    /**
     * LaTeX representation of the parameter/variable, e.g.&nbsp; {@code d_1} for the parameter d₁.
     */
    private String notation;

    /**
     * A sentence defining the parameter.
     */
    private String definition;

    /**
     * Creates a parameter of a formula.
     *
     * @param notation LaTeX representation of the parameter/variable, e.g.&nbsp; {@code d_1} for the parameter d₁.
     * @param definition A sentence defining the parameter.
     */
    public Parameter(String notation, String definition) {
        this.notation = notation;
        this.definition = definition;
    }

    /**
     * Clone the object.
     *
     * @return the cloned object
     */
    @Override
    public Object clone() {
        try {
            return (Parameter) super.clone();
        } catch (CloneNotSupportedException e) {
            return new Parameter(this.notation, this.definition);
        }
    }
}
