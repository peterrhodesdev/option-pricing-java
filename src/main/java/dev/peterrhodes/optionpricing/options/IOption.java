package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.models.AnalyticalCalculation;

interface IOption {
    double price();
    double delta();
    AnalyticalCalculation analyticalCalculation();
}
