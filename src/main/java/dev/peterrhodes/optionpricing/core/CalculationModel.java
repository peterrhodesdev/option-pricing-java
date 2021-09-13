package dev.peterrhodes.optionpricing.core;

import lombok.Getter;

@Getter
public abstract class CalculationModel {

    /**
     * TODO
     */
    private double price;

    /**
     * Creates a model for the results of an option calculation.
     * @param price theoretical option value
     */
    public CalculationModel(double price) {
        this.price = price;
    }
}
