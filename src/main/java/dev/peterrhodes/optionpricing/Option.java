package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.pricers.CoxRossRubinsteinPricer;

/**
 * Defines an option with the ways that it can be valued.
 */
public interface Option {

    /**
     * Returns the contract that defines the option properties.
     *
     * @return the option contract
     */
    Contract contract();

    /**
     * Returns the price of the option calculated with the <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a> model.
     *
     * @param timeSteps Number of time steps in the tree.
     * @return option price
     * @throws IllegalArgumentException if {@code timeSteps} is not greater than zero
     */
    default double coxRossRubinsteinPrice(int timeSteps) throws IllegalArgumentException {
        return this.coxRossRubinsteinCalculation(timeSteps).getPrice();
    }

    /**
     * Returns the details of the option price calculation performed by the <a href="https://citeseerx.ist.psu.edu/viewdoc/summary?doi=10.1.1.379.7582">Cox, Ross, and Rubinstein (1979)</a> model.
     *
     * @param timeSteps Number of time steps in the tree.
     * @return option price calculation details
     * @throws IllegalArgumentException if {@code timeSteps} is not greater than zero
     */
    default CoxRossRubinsteinModel coxRossRubinsteinCalculation(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
        CoxRossRubinsteinPricer pricer = new CoxRossRubinsteinPricer(this.contract(), timeSteps);
        return pricer.calculation();
    }
}
