package dev.peterrhodes.optionpricing.internal.common;

import dev.peterrhodes.optionpricing.internal.enums.LatexDelimeterType;
import dev.peterrhodes.optionpricing.internal.enums.PrecisionType;
import dev.peterrhodes.optionpricing.internal.utils.ValidationUtils;

/**
 * Holds the details of an input that is to be substituted into a LaTeX mathemtical expression.
 */
public class EquationInput implements PublicCloneable<EquationInput> {

    private final String key;
    private final Number numberValue;
    private final String stringValue;
    private LatexDelimeterType latexDelimeterType;
    private Integer precisionDigits;
    private PrecisionType precisionType;

    private EquationInput(Builder builder) {
        this.key = builder.key;
        this.numberValue = builder.numberValue;
        this.stringValue = builder.stringValue;
        this.latexDelimeterType = builder.latexDelimeterType;
        this.precisionDigits = builder.precisionDigits;
        this.precisionType = builder.precisionType;
    }

    /**
     * Builder class.
     */
    public static class Builder {
        private final String key;
        private Number numberValue;
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
        public Builder(String key) throws NullPointerException, IllegalArgumentException {
            ValidationUtils.checkNotNull(key, "key");
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
        public Builder withDelimeter(LatexDelimeterType latexDelimeterType) throws NullPointerException {
            ValidationUtils.checkNotNull(latexDelimeterType, "latexDelimeterType");
            this.latexDelimeterType = latexDelimeterType;
            return this;
        }

        /**
         * Use this to set the substitution value if it extends {@link java.lang.Number} (e.g.&nbsp;{@link java.lang.Double}, {@link java.lang.Integer}, ...).
         * <p>Note: if the number has trailing zeros and you want to keep the precision when it's substituted in, then cast it to a {@link java.lang.String} and use {@link #withStringValue(String)} instead.</p>
         *
         * @param numberValue The number value to be substituted into the equation for the key.
         * @return the current {@link Builder} object
         * @throws NullPointerException if {@code numberValue} is null
         * @throws IllegalStateException if {@link #withStringValue(String)} has been called
         */
        public Builder withNumberValue(Number numberValue) throws NullPointerException, IllegalStateException {
            ValidationUtils.checkNotNull(numberValue, "numberValue");
            if (this.stringValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.numberValue = numberValue;
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
        public Builder withPrecision(Integer precisionDigits, PrecisionType precisionType) throws NullPointerException, IllegalStateException, IllegalArgumentException {
            ValidationUtils.checkNotNull(precisionDigits, "precisionDigits");
            ValidationUtils.checkNotNull(precisionType, "precisionType");
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
         * @param stringValue The string value to be substituted into the equation for the key.
         * @return the current {@link Builder} object
         * @throws NullPointerException if {@code stringValue} is null
         * @throws IllegalStateException if {@link #withNumberValue(Number)} has been called
         */
        public Builder withStringValue(String stringValue) throws NullPointerException, IllegalStateException {
            ValidationUtils.checkNotNull(stringValue, "stringValue");
            if (this.numberValue != null) {
                throw new IllegalStateException("can only call either withNumberValue or withStringValue, not both");
            }
            this.stringValue = stringValue;
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

    //region getters
    //----------------------------------------------------------------------

    /**
     * Get key.
     *
     * @return key
     */
    public String getKey() {
        return this.key;
    }

    /**
     * Get numberValue.
     *
     * @return numberValue
     */
    public Number getNumberValue() {
        return this.numberValue;
    }

    /**
     * Get stringValue.
     *
     * @return stringValue
     */
    public String getStringValue() {
        return this.stringValue;
    }

    /**
     * Get latexDelimeterType.
     *
     * @return latexDelimeterType
     */
    public LatexDelimeterType getLatexDelimeterType() {
        return this.latexDelimeterType;
    }

    /**
     * Get precisionDigits.
     *
     * @return precisionDigits
     */
    public Integer getPrecisionDigits() {
        return this.precisionDigits;
    }

    /**
     * Get precisionType.
     *
     * @return precisionType
     */
    public PrecisionType getPrecisionType() {
        return this.precisionType;
    }

    //----------------------------------------------------------------------
    //endregion getters
}
