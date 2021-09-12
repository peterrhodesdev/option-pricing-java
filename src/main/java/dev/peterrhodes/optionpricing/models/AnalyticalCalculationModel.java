package dev.peterrhodes.optionpricing.models;

import dev.peterrhodes.optionpricing.core.CalculationModel;

import lombok.Getter;

@Getter
public class AnalyticalCalculationModel extends CalculationModel {

    private double delta;
    private double gamma;
    private double vega;
    private double theta;
    private double rho;

    /**
     * Creates a model for the results of an analytical calculation.
     * @param price theoretical option value
     * @param delta (Δ) first derivative of the option price with respect to the underlying asset price
     * @param gamma (Γ) second derivative of the option price with respect to the underlying asset price
     * @param vega first derivative of the option price with respect to the underlying asset volatility
     * @param theta (Θ) negative first derivative of the option value with respect to the time to maturity
     * @param rho (Ρ) first derivative of the option value with respect to the risk free interest rate
     */
    public AnalyticalCalculationModel(double price, double delta, double gamma, double vega, double theta, double rho) {
        super(price);
        this.delta = delta;
        this.gamma = gamma;
        this.vega = vega;
        this.theta = theta;
        this.rho = rho;
    }
}
