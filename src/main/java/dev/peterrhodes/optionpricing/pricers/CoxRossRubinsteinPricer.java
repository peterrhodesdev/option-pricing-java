package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.core.Pricer;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.common.NotImplementedException;

public class CoxRossRubinsteinPricer implements Pricer<CoxRossRubinsteinModel> {

    private int timeSteps;

    /**
     * TODO
     * @throws IllegalArgumentException if timeSteps is not greater than zero
     */
    public CoxRossRubinsteinPricer(int timeSteps) throws IllegalArgumentException {
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

    /**
     * TODO
     */
    @Override
    public CoxRossRubinsteinModel calculation(Option option) throws NotImplementedException {
        throw new NotImplementedException();
    }
}
