package dev.peterrhodes.optionpricing.core;

import java.util.Map;

/**
 * Interface for an option that doesn't have an analytical solution.&nbsp;If the specific option has an analytical solution then it will extend {@link AnalyticalOption}.
 */
public interface Option {

    /**
     * Calculates the value of exercising the option (assuming all preconditions are met).
     */
    double exerciseValue(double spotPrice);

    /**
     * Returns a map of the parameters/variables used to define the option.&nbsp;The key is the latex notation for the parameter and the value is its numeric value.
     */
    Map<String, String> optionParameters();
}
