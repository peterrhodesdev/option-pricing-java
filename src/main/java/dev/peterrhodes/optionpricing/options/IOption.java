package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.models.AnalyticalCalculation;

interface IOption {
    double price();
    double delta();
    double gamma();
    double vega();
    double theta();
    double rho();
    AnalyticalCalculation analyticalCalculation();
}
