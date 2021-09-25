package dev.peterrhodes.optionpricing.models;

import lombok.Getter;

/**
 * Base model for the details of an option price calculation.
 */
@Getter
public class CalculationModel {

    /**
     * The price of the option.
     */
    private double price;

    /**
     * Creates a model for the results of an option price calculation.
     */
    public CalculationModel(double price) {
        this.price = price;
    }
}
