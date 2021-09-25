package dev.peterrhodes.optionpricing;

/**
 * A customizable option.
 */
class OptionImpl implements Option {

    private Contract contract;

    public OptionImpl(Contract contract) {
        this.contract = contract;
    }

    @Override
    public Contract contract() {
        return this.contract;
    }
}
