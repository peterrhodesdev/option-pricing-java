package dev.peterrhodes.optionpricing.core;

import java.util.List;

/**
 * Interface for an option that has an analytical solution, i.e.&nbsp;can analytically calculate the option's value and its greeks.&nbsp;If the specific option doesn't have an analytical solution then it will extend {@link Option}.
 */
public interface AnalyticalOption extends Option {

    //region price
    //----------------------------------------------------------------------

    /**
     * Calculates the fair value of the option.
     *
     * @return option price
     */
    double price();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] priceFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] priceCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region delta
    //----------------------------------------------------------------------

    /**
     * Calculates the delta (Δ) of the option (first derivative of the option value with respect to the underlying asset price).
     *
     * @return option delta
     */
    double delta();

    /**
     * TODO.
     *
     * @return TODO
     */
    Formula deltaFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    Calculation deltaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region gamma
    //----------------------------------------------------------------------

    /**
     * Calculates the gamma (Γ) of the option (second derivative of the option value with respect to the underlying asset price).
     *
     * @return option gamma
     */
    double gamma();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] gammaFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] gammaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region vega
    //----------------------------------------------------------------------

    /**
     * Calculates the vega (ν) of the option (first derivative of the option value with respect to the underlying asset volatility).
     *
     * @return option vega
     */
    double vega();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] vegaFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] vegaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region theta
    //----------------------------------------------------------------------

    /**
     * Calculates the theta (Θ) of the option (negative first derivative of the option value with respect to the time to maturity).
     *
     * @return option theta
     */
    double theta();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] thetaFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] thetaCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region rho
    //----------------------------------------------------------------------

    /**
     * Calculates the rho (ρ) of the option (first derivative of the option value with respect to the risk free interest rate).
     *
     * @return option rho
     */
    double rho();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] rhoFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] rhoCalculation();

    //----------------------------------------------------------------------
    //endregion

    /**
     * Returns a list of the functions (e.g.&nbsp;the standard normal CDF) used in the calculation formulas.
     *
     * @return calculation functions
     */
    List<Parameter> calculationFunctions();
}
