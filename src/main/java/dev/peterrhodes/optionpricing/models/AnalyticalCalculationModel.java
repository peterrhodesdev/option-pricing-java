package dev.peterrhodes.optionpricing.models;

import lombok.Getter;

/**
 * Model containing the results of an analytical option calculation, i.e.&nbsp;the value of the option and its greeks.
 */
@Getter
public class AnalyticalCalculationModel {

    /**
     * Theoretical option value.
     */
    private double price;

    /**
     * Option delta (Δ) value: measures sensitivity to the underlying asset price.
     */
    private double delta;

    /**
     * Option gamma (Γ) value: measures sensitivity to the delta.
     */
    private double gamma;

    /**
     * Option vega (ν) value: measures sensitivity to the volatility.
     */
    private double vega;

    /**
     * Option theta (Θ) value: measures sensitivity to the passage of time.
     */
    private double theta;

    /**
     * Option rho (ρ) value: measures sensitivity to the risk free interest rate.
     */
    private double rho;

    /**
     * Creates a model for the results of an analytical option calculation.
     *
     * @param price Theoretical option value.
     * @param delta Option delta (Δ) value: measures sensitivity to the underlying asset price.
     * @param gamma Option gamma (Γ) value: measures sensitivity to the delta.
     * @param vega Option vega (ν) value: measures sensitivity to the volatility.
     * @param theta Option theta (Θ) value: measures sensitivity to the passage of time.
     * @param rho Option rho (ρ) value: measures sensitivity to the risk free interest rate.
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
