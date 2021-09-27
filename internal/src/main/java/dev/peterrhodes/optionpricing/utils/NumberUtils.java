package dev.peterrhodes.optionpricing.utils;

import dev.peterrhodes.optionpricing.enums.PrecisionType;

/**
 * Miscellaneous utility methods for the {@link java.lang.Number} class.
 */
public interface NumberUtils {

    /**
     * Return a {@link java.lang.String} formatted number with a given precision.&nbsp;Any rounding is performed according to the round half up rule.
     *
     * @param number the number to be formatted
     * @param digits number of digits of precision, ignored if {@code precisionType} is {@link PrecisionType#UNCHANGED}
     * @param precisionType type of precision for formatting
     * @return a {@link java.lang.String} of the number to the given precision
     * @throws NullPointerException if the {@code precisionType} is null
     * @throws IllegalStateException if the {@code precisionType} isn't {@link PrecisionType#UNCHANGED} and the {@code digits} is null or less than zero.
     */
    static String precision(Number number, Integer digits, PrecisionType precisionType) throws NullPointerException, IllegalStateException {
        precisionArgumentsCheck(digits, precisionType);

        switch (precisionType) {
            case DECIMAL_PLACES:
                return precisionDecimalPlaces(number, digits);
            case SIGNIFICANT_FIGURES:
                return precisionSignificantFigures(number, digits);
            case UNCHANGED:
            default:
                return number.toString();
        }
    }

    private static void precisionArgumentsCheck(Integer digits, PrecisionType precisionType) throws NullPointerException, IllegalStateException {
        ValidationUtils.checkNotNull(precisionType, "precisionType");
        if (precisionType != PrecisionType.UNCHANGED && (digits == null || digits < 0)) {
            throw new IllegalStateException("if {@code precisionType} isn't {@link PrecisionType#UNCHANGED} then {@code digits} can't be null or less than zero");
        }
    }

    private static String precisionDecimalPlaces(Number number, Integer digits) {
        return String.format("%." + digits.toString() + "f", number.doubleValue());
    }

    private static String precisionSignificantFigures(Number number, Integer digits) {
        String preciseNumber = String.format("%." + digits.toString() + "G", number.doubleValue());

        // If the amount of digits to the LHS of the decimal place of the number is greater than the precision, then it will be formatted in scientific notation
        if (preciseNumber.contains("e") || preciseNumber.contains("E")) {
            preciseNumber = String.format("%.0f", Double.parseDouble(preciseNumber));
        }

        return preciseNumber;
    }
}
