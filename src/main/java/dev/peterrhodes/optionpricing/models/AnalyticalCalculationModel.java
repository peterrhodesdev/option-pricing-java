package dev.peterrhodes.optionpricing.models;

import lombok.Getter;

/**
 * Model for the results of an analytical option calculation, i.e. the value of the option and its greeks.
 */
@Getter
public class AnalyticalCalculationModel {

    private double price;
    private double delta;
    private double gamma;
    private double vega;
    private double theta;
    private double rho;

    /**
     * Creates a model for the results of an analytical option calculation.
     *
     * @param price theoretical option value
     * @param delta option delta (Δ), measures sensitivity to the underlying asset price
     * @param gamma option gamma (Γ), measures sensitivity to the delta
     * @param vega option vega (ν), measures sensitivity to the volatility
     * @param theta option theta (Θ), measures sensitivity to the passage of time
     * @param rho option rho (ρ), measures sensitivity to the risk free interest rate
     */
    public AnalyticalCalculationModel(double price, double delta, double gamma, double vega, double theta, double rho) {
        this.price = price;
        this.delta = delta;
        this.gamma = gamma;
        this.vega = vega;
        this.theta = theta;
        this.rho = rho;
    }
}
