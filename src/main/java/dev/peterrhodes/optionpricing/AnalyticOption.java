package dev.peterrhodes.optionpricing;

import dev.peterrhodes.optionpricing.enums.PrecisionType;
import dev.peterrhodes.optionpricing.models.AnalyticCalculationModel;

/**
 * Interface for an option that has an analytical solution, i.e.&nbsp;can analytically calculate the option's value and its greeks.&nbsp;If the specific option doesn't have an analytical solution then it will extend {@link Option}.
 */
public interface AnalyticOption extends Option {

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
    AnalyticCalculationModel priceCalculation();

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
    AnalyticCalculationModel deltaCalculation();

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
    AnalyticCalculationModel gammaCalculation();

    /**
     * Calculates the value of vega of the option (first derivative of the option value with respect to the underlying asset volatility).
     *
     * @return option vega value
     */
    double vega();

    /**
     * Returns the details of the vega calculation for an option.
     *
     * @return vega calculation details
     */
    AnalyticCalculationModel vegaCalculation();

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
    AnalyticCalculationModel thetaCalculation();

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
    AnalyticCalculationModel rhoCalculation();

    /**
     * Sets the precision of the calculated values (not option parameters) for display in the LaTeX mathematical expressions.
     *
     * @param precisionDigits number of digits of precision
     * @param precisionType type of precision for formatting
     * @throws NullPointerException if {@code precisionType} is null
     * @throws IllegalArgumentException if {@code precisionDigits} is less than zero
     */
    void setCalculationStepPrecision(int precisionDigits, PrecisionType precisionType) throws NullPointerException, IllegalArgumentException;

    /**
     * List of the LaTeX notation used for the option parameters in the formulas.
     *
     * @return LaTeX notations
     */
    String[] parameterNotation();
}
