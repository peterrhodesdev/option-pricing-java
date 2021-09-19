package dev.peterrhodes.optionpricing.utils;

import dev.peterrhodes.optionpricing.enums.RoundingMethod;

/**
 * Miscellaneous utility methods for the {@link java.util.Number} class.
 */
public interface NumberUtils {

    /**
     * TODO.
     */
    static String round(Number number, Integer precisionDigits, RoundingMethod roundingMethod) {
        switch (roundingMethod) {
            case DECIMAL_PLACES:
                return String.format("%." + Integer.toString(precisionDigits) + "f", number);
            case SIGNIFICANT_FIGURES:
                return String.format("%." + Integer.toString(precisionDigits) + "G", number);
            case NONE:
            default:
                return number.toString();
        }
    }
}
