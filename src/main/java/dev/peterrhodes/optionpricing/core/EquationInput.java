package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import lombok.Getter;
import lombok.NonNull;

/**
 * Holds the details of an input that is to be substituted into a mathemtical equation.
 */
@Getter
public class EquationInput<T extends Number> implements PublicCloneable<EquationInput> {

    /**
     * Key for identifying the input variable name in an equation.
     */
    private final String key;

    /**
     * The number value to be substituted into the equation for the key.
     */
    private final T numberValue;

    /**
     * TODO.
     */
    private final String stringValue;

    /**
     * The type of delimeter to surround the value with when it's substituted into the equation.
     */
    private LatexDelimeterType latexDelimeterType;

    /**
     * TODO.
     */
    private Integer precisionDigits;

    /**
     * TODO.
     */
    private RoundingMethod roundingMethod;

    private EquationInput(Builder builder) {
        this.key = builder.key;
        this.numberValue = (T) builder.numberValue;
        this.stringValue = builder.stringValue;
        this.latexDelimeterType = builder.latexDelimeterType;
        this.precisionDigits = builder.precisionDigits;
        this.roundingMethod = builder.roundingMethod;
    }

    /**
     * TODO.
     */
    public boolean hasNumberValue() {
        return this.numberValue != null;
    }

    /**
     * TODO.
     */
    public boolean hasStringValue() {
        return this.stringValue != null;
    }

    /**
     * TODO.
     */
    public LatexDelimeterType setLatexDelimeterType(LatexDelimeterType latexDelimeterType) {
        return this.latexDelimeterType = latexDelimeterType;
    }

    /**
     * Clone the object.
     *
     * @return the cloned object
     */
    @Override
    public EquationInput clone() {
        try {
            return (EquationInput) super.clone();
        } catch (CloneNotSupportedException e) {
            Builder builder = new Builder(this.key)
                .withDelimeter(this.latexDelimeterType);

            if (this.numberValue != null) {
                builder = builder.withNumberValue(this.numberValue);

                if (this.precisionDigits != null && this.roundingMethod != null) {
                    builder = builder.withPrecision(this.precisionDigits, this.roundingMethod);
                }
            } else {
                builder = builder.withStringValue(this.stringValue);
            }
            
            return builder.build();   
        }
    }

    /**
     * TODO.
     */
    public static class Builder<N extends Number> {
        private final String key;
        private N numberValue;
        private String stringValue;
        private LatexDelimeterType latexDelimeterType;
        private Integer precisionDigits;
        private RoundingMethod roundingMethod;

        /**
         * <p>Builds an EquationInput object.</p>
         * TODO notes about flow
         * <ul>
         *   <li>{@code numberValue} = null</li>
         *   <li>{@code stringValue} = null</li>
         *   <li>{@code latexDelimeterType} = {@link LatexDelimeterType#NONE}</li>
         *   <li>{@code precisionDigits} = null</li>
         *   <li>{@code roundingMethod} = {@link RoundingMethod#NONE}</li>
         * </ul>
         *
         * @param key Key for identifying the input variable name in an equation.
         * @throws NullPointerException if {@code key} is null or empty/blank
         */
        public Builder(@NonNull String key) throws NullPointerException {
            if (key.trim().length() == 0) {
                throw new IllegalArgumentException("key can't be empty/blank");
            }
            this.key = key;

            // defaults
            this.numberValue = null;
            this.stringValue = null;
            this.latexDelimeterType = LatexDelimeterType.NONE;
            this.precisionDigits = null;
            this.roundingMethod = RoundingMethod.NONE;
        }

        /**
         * Sets the LaTeX delimeter type.
         *
         * @param latexDelimeterType The type of delimeter to surround the value with when it's substituted into the equation.
         * @throws NullPointerException if {@code latexDelimeterType} is null
         */
        public Builder withDelimeter(@NonNull LatexDelimeterType latexDelimeterType) throws NullPointerException {
            this.latexDelimeterType = latexDelimeterType;
            return this;
        }

        /**
         * Use this to set the substitution value if it extends {@link java.util.Number} (e.g.&nbsp;{@link java.util.Double}, {@link java.util.Integer}, ...).
         * <p>Note: if the number has trailing zeros and you want to keep the precision when it's substituted in, then cast it to a {@link java.util.String} and use {@link #withStringValue(String)} instead.</p>
         *
         * @param numberValue The number value to be substituted into the equation for the key.
         * @throws NullPointerException if {@code numberValue} is null
         * @throws IllegalStateException if {@link #withStringValue(String)} has been called
         */
        public Builder withNumberValue(@NonNull N numberValue) {
            if (this.stringValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.numberValue = numberValue;
            return this;
        }

        /**
         * TODO.
         */
        public Builder withPrecision(@NonNull Integer precisionDigits, @NonNull RoundingMethod roundingMethod) {
            if (this.numberValue == null) {
                throw new IllegalStateException();
            }
            this.precisionDigits = precisionDigits;
            this.roundingMethod = roundingMethod;
            return this;
        }

        /**
         * TODO.
         */
        public Builder withStringValue(@NonNull String value) {
            if (this.numberValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.stringValue = value;
            return this;
        }

        public EquationInput build() {
            return new EquationInput(this);
        }
    }
}
