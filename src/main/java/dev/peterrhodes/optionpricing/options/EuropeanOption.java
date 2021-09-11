package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;

public class EuropeanOption implements IOption {

    private OptionType optionType;
    private double S;
    private double K;
    private double T;
    private double σ;
    private double r;

    /**
     * Creates a European option with the specified parameters.
     * @param optionType type of the option (call or put)
     * @param S price of the underlying asset (spot price)
     * @param K strike price of the option (exercise price)
     * @param T time until option expiration (time from the start of the contract until maturity)
     * @param σ standard deviation of the underlying's returns (a measure of volatility)
     * @param r annualized risk-free interest rate, continuously compounded
     */
    public EuropeanOption(
        OptionType optionType,
        double S,
        double K,
        double T,
        double σ,
        double r
    ) {
        this.optionType = optionType;
        this.S = S;
        this.K = K;
        this.T = T;
        this.σ = σ;
        this.r = r;
    }

    public double analyticalPrice() {
        return 0.0;
    }
}
