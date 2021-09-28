package dev.peterrhodes.optionpricing;

/**
 * Interface for a financial option.
 */
public interface Option {

    /**
     * Returns the contract that defines the option properties.
     *
     * @return the option contract
     */
    Contract contract();
}
