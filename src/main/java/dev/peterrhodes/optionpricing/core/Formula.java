package dev.peterrhodes.optionpricing.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class Formula {

    private final String joiner = " = ";

    private String lhs;
    private String rhs;
    private String rhsSimplified;
    private List<String> whereComponents;
    private List<Parameter> parameters;

    //region constructors
    //----------------------------------------------------------------------

    /**
     * TODO.
     */
    public Formula(String lhs, String rhs, String rhsSimplified, List<String> whereComponents, List<Parameter> parameters) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.rhsSimplified = rhsSimplified;
        this.whereComponents = whereComponents;
        this.parameters = parameters;
    }

    /**
     * TODO.
     */
    public Formula(String lhs, String rhs) {
        this(lhs, rhs, null);
    }

    /**
     * TODO.
     */
    public Formula(String lhs, String rhs, String rhsSimplified) {
        this(lhs, rhs, rhsSimplified, new ArrayList<String>(), new ArrayList<Parameter>());
    }

    //----------------------------------------------------------------------
    //endregion constructors

    /**
     * TODO.
     */
    public String build() {
        // TODO refactor " = "
        return this.lhs + " = " + this.rhs + (this.rhsSimplified != null ? " = " + this.rhsSimplified : "");
    }
}
