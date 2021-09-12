package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.core.Pricer;

public class CoxRossRubinsteinBinomialOptionsPricingModel implements Pricer {

    private int timeSteps;

    /**
     * TODO
     * @throws IllegalArgumentException if timeSteps is not greater than zero
     */
    public CoxRossRubinsteinBinomialOptionsPricingModel(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
        this.timeSteps = timeSteps;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double price(Option option) {
        return 0.0;
    }
}
