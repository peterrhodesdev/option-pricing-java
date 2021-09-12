package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.models.AnalyticalCalculation;

public interface AnalyticalOption extends Option {

    /**
     * Calculates the fair value of the option.
     * @return option price
     */
    double price();

    double delta();
    double gamma();
    double vega();
    double theta();
    double rho();
    AnalyticalCalculation analyticalCalculation();
}
