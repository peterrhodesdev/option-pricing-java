package dev.peterrhodes.optionpricing;

/**
 * TODO.
 */
public interface PricingModel<T> {

    /**
     * TODO.
     */
    double price(Option option);

    /**
     * Returns a model with the details of the pricing model calculation.
     *
     * @param option TODO
     * @return calculation details model
     */
    T calculation(Option option);
}
