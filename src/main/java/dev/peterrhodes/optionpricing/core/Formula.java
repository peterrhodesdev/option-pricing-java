package dev.peterrhodes.optionpricing.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
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
     * <p>
     * Note: variables need to be separated from other text with a non-word character (i.e. not [a-z0-9A-Z_]) to allow for value substitution performed by {@link dev.peterrhodes.optionpricing.helpers.CalculationHelper CalculationHelper}. e.g.
     * </p>
     * <ul>
     *   <li>Write "<code>2 x</code>" instead of "<code>2 x</code>"
     *   <li>Write "<code>x y</code>" instead of "<code>xy</code>"
     * </ul>
     * <p>This doesn't affect the rendering of the LaTeX as whitespace is ignored.</p>
     */
    private String rhs;

    /**
     * List of calculation steps (written in LaTeX) to get from the LHS to the RHS.
     */
    @Getter(value = AccessLevel.NONE)
    private List<String> steps;

    /**
     * List of components (written in LaTeX) in the "where" clause of the formula, e.g.&nbsp;for the Black-Scholes formula this would include d₁ and d₂.
     */
    @Getter(value = AccessLevel.NONE)
    private List<String> whereComponents;

    //region constructors
    //----------------------------------------------------------------------

    /**
     * Creates a representation of a mathematical formula.
     *
     * @param lhs LaTeX for the left-hand-side (LHS) of the formula.
     * @param rhs LaTeX for the right-hand-side (RHS) of the formula.
     * @param steps List of calculation steps (in LaTeX) to get from the LHS to the RHS.
     * @param whereComponents List of components (written in LaTeX) in the "where" clause of the formula, e.g.&nbsp;for the Black-Scholes formula this would include d₁ and d₂.
     */
    public Formula(String lhs, String rhs, List<String> steps, List<String> whereComponents) {
        this.lhs = lhs;
        this.rhs = rhs;

        // Deep copy steps
        this.steps = new ArrayList();
        this.steps.addAll(steps);

        // Deep copy whereComponents
        this.whereComponents = new ArrayList();
        this.whereComponents.addAll(whereComponents);
    }

    /**
     * {@code steps} defaults to an empty List.
     *
     * @see #Formula(String, String, List)
     */
    public Formula(String lhs, String rhs) {
        this(lhs, rhs, new ArrayList<String>());
    }

    /**
     * {@code whereComponents} defaults to an empty list.
     *
     * @see #Formula(String, String, List, List)
     */
    public Formula(String lhs, String rhs, List<String> steps) {
        this(lhs, rhs, steps, new ArrayList<String>());
    }

    //----------------------------------------------------------------------
    //endregion constructors

    /**
     * Builds the LaTeX formula from the defined parts of the object.
     * TODO add example: x = 2(y + y) = 2(2 y) = 4 y
     *
     * @return LaTeX formula of the object
     */
    public String build() {
        List<String> formulaParts = new ArrayList();
        formulaParts.add(this.lhs);
        formulaParts.addAll(this.steps);
        formulaParts.add(this.rhs);

        return formulaParts.stream()
            .collect(Collectors.joining(" = "));
    }

    /**
     * Returns a deep copy of the steps list.
     *
     * @return steps
     */
    public List<String> getSteps() {
        List<String> deepCopy = new ArrayList();
        deepCopy.addAll(this.steps);
        return deepCopy;
    }

    /**
     * Returns a deep copy of the where components list.
     *
     * @return whereComponents
     */
    public List<String> getWhereComponents() {
        List<String> deepCopy = new ArrayList();
        deepCopy.addAll(this.whereComponents);
        return deepCopy;
    }
}
