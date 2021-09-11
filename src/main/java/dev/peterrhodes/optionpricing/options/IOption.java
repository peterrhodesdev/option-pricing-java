package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.models.AnalyticalCalculation;

interface IOption {
    double analyticalPrice();
    AnalyticalCalculation analyticalCalculation();
}
