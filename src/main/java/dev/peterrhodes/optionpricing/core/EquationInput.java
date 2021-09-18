package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import lombok.Getter;
import lombok.NonNull;

/**
 * Holds the details of an input that is to be substituted into a mathemtical equation.
 */
@Getter
public class EquationInput<T extends Number> implements Cloneable {

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

    /**
     * TODO.
     */
    private boolean precisionImmutable;

    private EquationInput(Builder builder) {
        this.key = builder.key;
        this.numberValue = (T) builder.numberValue;
        this.stringValue = builder.stringValue;
        this.latexDelimeterType = builder.latexDelimeterType;
        this.precisionDigits = builder.precisionDigits;
        this.roundingMethod = builder.roundingMethod;
        this.precisionImmutable = builder.precisionImmutable;
    }

    /**
     * Clone the object.
     *
     * @return the cloned object
     */
    @Override
    public Object clone() {
        Builder builder = new Builder(this.key)
            .withDelimeter(this.latexDelimeterType);

        if (this.numberValue != null) {
            builder = builder.withNumberValue(this.numberValue);

            if (this.precisionDigits != null && this.roundingMethod != null) {
                builder = builder.withPrecision(this.precisionDigits, this.roundingMethod, this.precisionImmutable);
            }
        } else {
            builder = builder.withStringValue(this.stringValue);
        }

        return builder.build();
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
        private boolean precisionImmutable;

        /**
         * <p>Builds an EquationInput object.</p>
         * TODO notes about flow
         * <ul>
         *   <li>{@code numberValue} = null</li>
         *   <li>{@code stringValue} = null</li>
         *   <li>{@code latexDelimeterType} = {@link LatexDelimeterType#NONE}</li>
         *   <li>{@code precisionDigits} = null</li>
         *   <li>{@code roundingMethod} = {@link RoundingMethod#NONE}</li>
         * TODO immutable
         * </ul>
         *
         * @param key Key for identifying the input variable name in an equation.
         * @throws IllegalArgumentException if {@code key} is null or empty/blank
         */
        public Builder(@NonNull String key) throws IllegalArgumentException {
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
            this.precisionImmutable = true;
        }

        /**
         * Sets the LaTeX delimeter type.
         *
         * @param latexDelimeterType The type of delimeter to surround the value with when it's substituted into the equation.
         * @throws IllegalArgumentException if {@code latexDelimeterType} is null
         */
        public Builder withDelimeter(@NonNull LatexDelimeterType latexDelimeterType) throws IllegalArgumentException {
            this.latexDelimeterType = latexDelimeterType;
            return this;
        }

        /**
         * Use this to set the substitution value if it extends {@link java.util.Number} (e.g.&nbsp;{@link java.util.Double}, {@link java.util.Integer}, ...).
         * <p>Note: if the number has trailing zeros and you want to keep the precision when it's substituted in, then cast it to a {@link java.util.String} and use {@link #withStringValue(String)} instead.</p>
         *
         * @param numberValue The number value to be substituted into the equation for the key.
         * @throws IllegalArgumentException if {@code numberValue} is null
         * @throws IllegalStateException if {@link #withStringValue(String)} has been called
         */
        public Builder withNumberValue(N numberValue) {
            if (this.stringValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.numberValue = numberValue;
            return this;
        }

        /**
         * TODO.
         */
        public Builder withPrecision(@NonNull Integer precisionDigits, @NonNull RoundingMethod roundingMethod, boolean immutable) {
            if (this.numberValue == null) {
                throw new IllegalStateException();
            }
            this.precisionDigits = precisionDigits;
            this.roundingMethod = roundingMethod;
            this.precisionImmutable = immutable;
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
