package dev.peterrhodes.optionpricing.core;

import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class Parameter {

    private String notation;
    private String definition;

    /**
     * TODO.
     */
    public Parameter(String notation, String definition) {
        this.notation = notation;
        this.definition = definition;
    }
}
