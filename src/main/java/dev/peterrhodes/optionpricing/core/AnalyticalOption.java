package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.models.AnalyticalCalculationModel;

/**
 * Interface for an option that has an analytical solution, i.e. can analytically calculate the option's value and its greeks.
 * If the specific option doesn't have an analytical solution then it will extend {@link Option}.
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
    String[] priceLatexFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] priceLatexCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region delta
    //----------------------------------------------------------------------

    /**
     * Calculates the delta (Δ) of the option.
     * First derivative of the option value with respect to the underlying asset price.
     *
     * @return option delta
     */
    double delta();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] deltaLatexFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] deltaLatexCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region gamma
    //----------------------------------------------------------------------

    /**
     * Calculates the gamma (Γ) of the option.
     * Second derivative of the option value with respect to the underlying asset price.
     *
     * @return option gamma
     */
    double gamma();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] gammaLatexFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] gammaLatexCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region vega
    //----------------------------------------------------------------------

    /**
     * Calculates the vega (ν) of the option.
     * First derivative of the option value with respect to the underlying asset volatility.
     *
     * @return option vega
     */
    double vega();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] vegaLatexFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] vegaLatexCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region theta
    //----------------------------------------------------------------------

    /**
     * Calculates the theta (Θ) of the option.
     * Negative first derivative of the option value with respect to the time to maturity.
     *
     * @return option theta
     */
    double theta();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] thetaLatexFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] thetaLatexCalculation();

    //----------------------------------------------------------------------
    //endregion

    //region rho
    //----------------------------------------------------------------------

    /**
     * Calculates the rho (ρ) of the option.
     * First derivative of the option value with respect to the risk free interest rate.
     *
     * @return option rho
     */
    double rho();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] rhoLatexFormula();

    /**
     * TODO.
     *
     * @return TODO
     */
    String[] rhoLatexCalculation();

    //----------------------------------------------------------------------
    //endregion

    /**
     * Performs all of the calculations necessary to populate an {@link AnalyticalCalculationModel}.
     *
     * @return model object populated with the results of the calculations
     */
    AnalyticalCalculationModel calculation();
}
