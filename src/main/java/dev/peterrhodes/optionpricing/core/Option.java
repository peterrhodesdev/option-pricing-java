package dev.peterrhodes.optionpricing.core;

import java.util.Map;

/**
 * Interface for an option that doesn't have an analytical solution.&nbsp;If the specific option has an analytical solution then it will extend {@link AnalyticalOption}.
 */
public interface Option {

    /**
     * TODO.
     */
    Map<String, String> optionParameters();
}
