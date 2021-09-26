package dev.peterrhodes.optionpricing.models;

/**
 * Base model for the details of an option price calculation.
 */
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

    //region getters
    //----------------------------------------------------------------------

    /**
     * Get price.
     *
     * @return price
     */
    public double getPrice() {
        return this.price;
    }

    //----------------------------------------------------------------------
    //endregion getters
}
