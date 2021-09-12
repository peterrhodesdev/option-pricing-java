package dev.peterrhodes.optionpricing.core;

public interface Pricer<T extends CalculationModel> {

    /**
     * TODO
     */
    double price(Option option);

    /**
     * TODO
     */
    T calculation(Option option);
}
