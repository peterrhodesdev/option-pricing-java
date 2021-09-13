package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;

public interface AnalyticalOption extends Option {

    /**
     * Calculates the fair value of the option.
     * @return option price
     */
    double price();

    /**
     * TODO
     */
    double delta();
    double gamma();
    double vega();
    double theta();
    double rho();

    AnalyticalCalculationModel calculation();
}
