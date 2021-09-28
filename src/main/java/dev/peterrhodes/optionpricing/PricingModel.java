package dev.peterrhodes.optionpricing;

/**
 * Interface for an option pricing model.
 */
public interface PricingModel<T> {

    /**
     * Calculates the price of the option.
     *
     * @param option the option to be priced
     * @return option price
     * @throws NullPointerException if {@code option} is null
     */
    double price(Option option) throws NullPointerException;

    /**
     * Returns a model with the details of the option pricing model calculation.
     *
     * @param option the option to perform the calculation on
     * @return calculation details model
     * @throws NullPointerException if {@code option} is null
     */
    T calculation(Option option) throws NullPointerException;
}
