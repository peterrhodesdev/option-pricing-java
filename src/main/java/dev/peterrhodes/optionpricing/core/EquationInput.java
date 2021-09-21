package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.enums.PrecisionType;
import lombok.Getter;
import lombok.NonNull;

/**
 * Holds the details of an input that is to be substituted into a LaTeX mathemtical expression.
 */
@Getter
public class EquationInput<T extends Number> implements PublicCloneable<EquationInput> {

    /**
     * Key for identifying the input variable name in a LaTeX mathematical expression.
     */
    private final String key;

    /**
     * The numerical value to be substituted into the equation for the key.&nbsp;If the value isn't a number, then the string value should be set instead.
     */
    private final T numberValue;

    /**
     * The string value to be substituted into the equation for the key.&nbsp;If the value is a number, then the number value should be set instead.
     */
    private final String stringValue;

    /**
     * The type of delimeter to surround the value with when it's substituted into the equation.
     */
    private LatexDelimeterType latexDelimeterType;

    /**
     * Number of digits of precision to display in the LaTeX mathematical expression.
     */
    private Integer precisionDigits;

    /**
     * Type of precision for formatting the number value.
     */
    private PrecisionType precisionType;

    private EquationInput(Builder builder) {
        this.key = builder.key;
        this.numberValue = (T) builder.numberValue;
        this.stringValue = builder.stringValue;
        this.latexDelimeterType = builder.latexDelimeterType;
        this.precisionDigits = builder.precisionDigits;
        this.precisionType = builder.precisionType;
    }

    /**
     * Check whether the number value of the object has been set.
     *
     * @return true if the number value has been set, false otherwise (i.e.&nbsp;the string value has been set)
     */
    public boolean hasNumberValue() {
        return this.numberValue != null;
    }

    /**
     * Check whether the string value of the object has been set.
     *
     * @return true if the string value has been set, false otherwise (i.e.&nbsp;the string value has been set)
     */
    public boolean hasStringValue() {
        return this.stringValue != null;
    }

    /**
     * Sets the type of LaTeX delimeter to surround the value with when it's substituted into a LaTeX mathematical expression.
     *
     * @param latexDelimeterType the type of LaTeX delimeter
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

                if (this.precisionDigits != null && this.precisionType != null) {
                    builder = builder.withPrecision(this.precisionDigits, this.precisionType);
                }
            } else {
                builder = builder.withStringValue(this.stringValue);
            }
            
            return builder.build();   
        }
    }

    /**
     * Builder class for {@link EquationInput}.
     */
    public static class Builder<N extends Number> {
        private final String key;
        private N numberValue;
        private String stringValue;
        private LatexDelimeterType latexDelimeterType;
        private Integer precisionDigits;
        private PrecisionType precisionType;

        /**
         * Builds an EquationInput object.
         * <p>Note: {@link #withNumberValue(Number)} must be called before {@link #withPrecision(Integer, PrecisionType)}. The default precision type is {@link PrecisionType#UNCHANGED}</p>
         *
         * @param key Key for identifying the input variable name in an equation.
         * @throws NullPointerException if {@code key} is null
         * @throws IllegalArgumentException if {@code key} is empty or blank
         */
        public Builder(@NonNull String key) throws NullPointerException, IllegalArgumentException {
            if (key.trim().length() == 0) {
                throw new IllegalArgumentException("key can't be empty/blank");
            }
            this.key = key;

            // defaults
            this.numberValue = null;
            this.stringValue = null;
            this.latexDelimeterType = LatexDelimeterType.NONE;
            this.precisionDigits = null;
            this.precisionType = PrecisionType.UNCHANGED;
        }

        /**
         * Sets the LaTeX delimeter type.
         *
         * @param latexDelimeterType The type of delimeter to surround the value with when it's substituted into the equation.
         * @return the current {@link Builder} object
         * @throws NullPointerException if {@code latexDelimeterType} is null
         */
        public Builder withDelimeter(@NonNull LatexDelimeterType latexDelimeterType) throws NullPointerException {
            this.latexDelimeterType = latexDelimeterType;
            return this;
        }

        /**
         * Use this to set the substitution value if it extends {@link java.lang.Number} (e.g.&nbsp;{@link java.lang.Double}, {@link java.lang.Integer}, ...).
         * <p>Note: if the number has trailing zeros and you want to keep the precision when it's substituted in, then cast it to a {@link java.lang.String} and use {@link #withStringValue(String)} instead.</p>
         *
         * @param value The number value to be substituted into the equation for the key.
         * @return the current {@link Builder} object
         * @throws NullPointerException if {@code value} is null
         * @throws IllegalStateException if {@link #withStringValue(String)} has been called
         */
        public Builder withNumberValue(@NonNull N value) throws NullPointerException, IllegalStateException {
            if (this.stringValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.numberValue = value;
            return this;
        }

        /**
         * Use this to set the precision of the number value for display in LaTeX mathematical expressions.
         *
         * @param precisionDigits number of digits of precision
         * @param precisionType type of precision
         * @return the current {@link Builder} object
         * @throws NullPointerException if {@code precisionDigits} or {@code precisionType} is null
         * @throws IllegalStateException if {@link #withNumberValue(Number)} has not been called
         * @throws IllegalArgumentException if {@code precisionDigits} is less than zero
         */
        public Builder withPrecision(@NonNull Integer precisionDigits, @NonNull PrecisionType precisionType) throws NullPointerException, IllegalStateException, IllegalArgumentException {
            if (this.numberValue == null) {
                throw new IllegalStateException("the number value must be set before calling this method");
            }
            if (precisionDigits < 0) {
                throw new IllegalArgumentException("precisionDigits must be greater than or equal to zero");
            }
            this.precisionDigits = precisionDigits;
            this.precisionType = precisionType;
            return this;
        }

        /**
         * Use this to set the substitution value as a {@link java.lang.String}.
         *
         * @param value The string value to be substituted into the equation for the key.
         * @return the current {@link Builder} object
         * @throws NullPointerException if {@code value} is null
         * @throws IllegalStateException if {@link #withNumberValue(Number)} has been called
         */
        public Builder withStringValue(@NonNull String value) throws NullPointerException, IllegalStateException {
            if (this.numberValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.stringValue = value;
            return this;
        }

        /**
         * Builds the {@link EquationInput} object.
         *
         * @return the equation input object
         */
        public EquationInput build() {
            return new EquationInput(this);
        }
    }
}
