package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.RoundingMethod;
import dev.peterrhodes.optionpricing.models.CalculationModel;

/**
 * Interface for an option that has an analytical solution, i.e.&nbsp;can analytically calculate the option's value and its greeks.&nbsp;If the specific option doesn't have an analytical solution then it will extend {@link Option}.
 */
public interface AnalyticalOption extends Option {

    //region price
    //----------------------------------------------------------------------

    /**
     * Calculates the fair value (price) of the option.
     *
     * @return option price
     */
    double price();

    /**
     * Returns the details of the price calculation for an option.
     *
     * @return price calculation details
     */
    CalculationModel priceCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region delta
    //----------------------------------------------------------------------

    /**
     * Calculates the value of delta (Δ) of the option (first derivative of the option value with respect to the underlying asset price).
     *
     * @return option delta
     */
    double delta();

    /**
     * Returns the details of the delta (Δ) calculation for an option.
     *
     * @return delta calculation details
     */
    CalculationModel deltaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region gamma
    //----------------------------------------------------------------------

    /**
     * Calculates the value of gamma (Γ) of the option (second derivative of the option value with respect to the underlying asset price).
     *
     * @return option gamma value
     */
    double gamma();

    /**
     * Returns the details of the gamma (Γ) calculation for an option.
     *
     * @return gamma calculation details
     */
    CalculationModel gammaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region vega
    //----------------------------------------------------------------------

    /**
     * Calculates the value of vega (ν) of the option (first derivative of the option value with respect to the underlying asset volatility).
     *
     * @return option vega value
     */
    double vega();

    /**
     * Returns the details of the vega (ν) calculation for an option.
     *
     * @return vega calculation details
     */
    CalculationModel vegaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region theta
    //----------------------------------------------------------------------

    /**
     * Calculates the value of theta (Θ) of the option (negative first derivative of the option value with respect to the time to maturity).
     *
     * @return option theta value
     */
    double theta();

    /**
     * Returns the details of the theta (Θ) calculation for an option.
     *
     * @return theta calculation details
     */
    CalculationModel thetaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region rho
    //----------------------------------------------------------------------

    /**
     * Calculates the value of rho (ρ) of the option (first derivative of the option value with respect to the risk free interest rate).
     *
     * @return option rho value
     */
    double rho();

    /**
     * Returns the details of the rho (ρ) calculation for an option.
     *
     * @return rho calculation details
     */
    CalculationModel rhoCalculation();

    //----------------------------------------------------------------------
    //endregion

    /**
     * TODO.
     */
    void setCalculationStepPrecision(int precision, RoundingMethod roundingMethod);
}
