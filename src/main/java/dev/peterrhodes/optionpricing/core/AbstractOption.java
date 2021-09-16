package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import lombok.Getter;

/**
 * Base class for all concrete option classes that don't have an analytical solution.&nbsp;If the specific option has an analytical solution then it should extend {@link AbstractAnalyticalOption}.
 */
@Getter
public abstract class AbstractOption implements Option {

    /**
     * Style style of the option, usually defined by the exercise rights, e.g.&nbsp;European, American.
     */
    protected OptionStyle style;

    /**
     * Type of the option (call or put).
     */
    protected OptionType type;

    /**
     * Price of the underlying asset (spot price) ({@code S}).
     */
    protected double S;

    /**
     * Strike price of the option (exercise price) ({@code S}).
     */
    protected double K;

    /**
     * Time until option expiration (time from the start of the contract until maturity) ({@code T}).
     */
    protected double T;

    /**
     * Underlying volatility (standard deviation of log returns) ({@code σ}).
     */
    protected double vol;

    /**
     * Annualized risk-free interest rate, continuously compounded ({@code r}).
     */
    protected double r;

    /**
     * Continuous dividend yield ({@code q}).
     */
    protected double q;

    /**
     * Creates an abstract option with the specified parameters.
     *
     * @param style {@link #style}
     * @param type {@link #type}
     * @param S {@link #S}
     * @param K {@link #K}
     * @param T {@link #T}
     * @param vol {@link #vol}
     * @param r {@link #r}
     * @param q {@link #q}
     * @throws IllegalArgumentException if {@code S}, {@code K}, {@code T}, or {@code vol} are not greater than zero
     */
    public AbstractOption(OptionStyle style, OptionType type, double S, double K, double T, double vol, double r, double q) throws IllegalArgumentException {
        this.style = style;
        this.type = type;
        this.S = this.checkGreaterThanZero(S, "S");
        this.K = this.checkGreaterThanZero(K, "K");
        this.T = this.checkGreaterThanZero(T, "T");
        this.vol = this.checkGreaterThanZero(vol, "vol (σ)");
        this.r = r;
        this.q = q;
    }

    private double checkGreaterThanZero(double value, String name) throws IllegalArgumentException {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
        return value;
    }
}
