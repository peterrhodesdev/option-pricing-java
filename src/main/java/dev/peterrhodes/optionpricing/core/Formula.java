package dev.peterrhodes.optionpricing.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * Represents mathematical formula.
 */
@Getter
public class Formula {

    /**
     * LaTeX for the left-hand-side (LHS) of the formula.
     */
    private String lhs;

    /**
     * LaTeX for the right-hand-side (RHS) of the formula.
     */
    private String rhs;

    /**
     * LaTeX for a simplified version of the right-hand-side (RHS) of the formula.&nbsp;Set to {@code null} if the {@code rhs} is already in simplified format.
     */
    private String rhsSimplified;

    /**
     * LaTeX for each component in the "where" clause of the formula, e.g.&nbsp;for the Black-Scholes formula this would include {@code d₁} and {@code d₂}.
     */
    private List<String> whereComponents;

    /**
     * List of the parameters/variables used in the formula, e.g.&nbsp;spot price ({@code S}), strike price ({@code K}), ...
     */
    private List<Parameter> parameters;

    //region constructors
    //----------------------------------------------------------------------

    /**
     * Creates a representation of a mathematical formula.
     *
     * @param lhs LaTeX for the left-hand-side (LHS) of the formula.
     * @param rhs LaTeX for the right-hand-side (RHS) of the formula.
     * @param rhsSimplified LaTeX for a simplified version of the right-hand-side (RHS) of the formula.&nbsp;Set to {@code null} if the {@code rhs} is already in simplified format.
     * @param whereComponents LaTeX for each component in the "where" clause of the formula, e.g.&nbsp;for the Black-Scholes formula this would include {@code d₁} and {@code d₂}.
     * @param parameters List of the parameters/variables used in the formula, e.g.&nbsp;spot price ({@code S}), strike price ({@code K}), ...
     */
    public Formula(String lhs, String rhs, String rhsSimplified, List<String> whereComponents, List<Parameter> parameters) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.rhsSimplified = rhsSimplified;
        this.whereComponents = whereComponents;
        this.parameters = parameters;
    }

    /**
     * {@code rhsSimplified} defaults to {@code null}.
     *
     * @see #Formula(String, String, String)
     */
    public Formula(String lhs, String rhs) {
        this(lhs, rhs, null);
    }

    /**
     * {@code whereComponents} and {@code parameters} default to empty lists.
     *
     * @see #Formula(String, String, String, List, List)
     */
    public Formula(String lhs, String rhs, String rhsSimplified) {
        this(lhs, rhs, rhsSimplified, new ArrayList<String>(), new ArrayList<Parameter>());
    }

    //----------------------------------------------------------------------
    //endregion constructors

    /**
     * Builds the LaTeX formula from the defined parts of the object, e.g.&nbsp;if {@code lhs = x}, {@code rhs = y + y}, and {@code rshSimplified = 2y}, the generated formula will be {@code x = y + y = 2y}.&nbsp;If {@code rshSimplified} is {@code null} then it will be omitted.
     *
     * @return LaTeX formula of the object
     */
    public String build() {
        return this.lhs + " = " + this.rhs + (this.rhsSimplified != null ? " = " + this.rhsSimplified : "");
    }
}
