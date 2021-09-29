package dev.peterrhodes.optionpricing.internal.utils;

import java.util.Map;

/**
 * Miscellaneous utility methods for validation.
 */
public interface ValidationUtils {

    /**
     * Checks whether an object is null.
     *
     * @param object the object to check
     * @param description added to the exception message
     * @throws NullPointerException if the value is null
     */
    static void checkNotNull(Object object, String description) throws NullPointerException {
        if (object == null) {
            throw new NullPointerException(description + " can't be null");
        }
    }

    /**
     * Checks whether a map's (Object) values are null.
     *
     * @param map the map values to check (the keys are a description added to the exception message)
     * @throws NullPointerException if any of the map values are null
     */
    static void checkNotNull(Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            checkNotNull(entry.getValue(), entry.getKey());
        }
    }

    /**
     * Checks whether a number is greater than zero.
     */
    static void checkGreaterThanZero(Number number, String description) throws IllegalArgumentException {
        if (number.doubleValue() <= 0) {
            throw new IllegalArgumentException(description + " must be greater than zero");
        }
    }
}
