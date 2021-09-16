package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.BracketType;
import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class EquationInput {

    private String key;
    private String value;
    private BracketType bracketType;

    /**
     * TODO.
     */
    public EquationInput(String key, String value, BracketType bracketType) {
        this.key = key;
        this.value = value;
        this.bracketType = bracketType;
    }

    /**
     * TODO.
     */
    public EquationInput(String key, String value) {
        this(key, value, BracketType.NONE);
    }
}
