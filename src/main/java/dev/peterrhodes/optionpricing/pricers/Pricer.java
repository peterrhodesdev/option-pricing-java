package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.models.CalculationModel;

interface Pricer<T extends CalculationModel> {

    /**
     * Returns the details of the pricer calculation.
     *
     * @return calculation details
     */
    T calculation();
}
