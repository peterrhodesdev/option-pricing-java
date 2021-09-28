package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.internal.pricingmodels.CoxRossRubinsteinPricingModel;
import dev.peterrhodes.optionpricing.models.CoxRossRubinstein;

/**
 * TODO.
 */
public interface PricingModelSelector {

    /**
     * Returns a {@link PricingModel} which implements the <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a> pricing model.
     *
     * @param timeSteps number of time steps in the tree
     * @return pricing model
     * @throws IllegalArgumentException if {@code timeSteps} is not greater than zero
     */
    static PricingModel<CoxRossRubinstein> coxRossRubinstein(int timeSteps) throws IllegalArgumentException {
        return new CoxRossRubinsteinPricingModel(timeSteps);
    }
}
