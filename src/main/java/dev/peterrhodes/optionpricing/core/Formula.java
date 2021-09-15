package dev.peterrhodes.optionpricing.core;

import java.util.List;
import java.util.Optional;
import lombok.Getter;

/**
 * TODO.
 */
@Getter
public class Formula {

    /**
     * TODO.
     */
    @Getter
    public static class Function {
        private String equation;
        private String definition;

        /**
         * TODO.
         */
        public Function(String equation, String definition) {
            this.equation = equation;
            this.definition = definition;
        }
    }

    /**
     * TODO.
     */
    @Getter
    public static class Parameter {
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

    private String lhs;
    private String rhs;
    private Optional<String> alt;
    private List<String> whereComponents;
    private List<Function> functions;
    private List<Parameter> parameters;

    /**
     * TODO.
     */
    public Formula(String lhs, String rhs, Optional<String> alt, List<String> whereComponents, List<Function> functions, List<Parameter> parameters) {
        this.lhs = lhs;
        this.rhs = rhs;
        this.alt = alt;
        this.whereComponents = whereComponents;
        this.parameters = parameters;
        this.functions = functions;
    }
}
