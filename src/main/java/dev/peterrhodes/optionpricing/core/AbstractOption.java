package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.Map;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;

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
     * Price of the underlying asset (spot price) (S).
     */
    protected double S;

    /**
     * Strike price of the option (exercise price) (K).
     */
    protected double K;

    /**
     * Time until option expiration (time from the start of the contract until maturity) (T).
     */
    protected double T;

    /**
     * Underlying volatility (standard deviation of log returns) (σ).
     */
    protected double vol;

    /**
     * Annualized risk-free interest rate (continuously compounded) (r).
     */
    protected double r;

    /**
     * Annual dividend yield (continuously compounded) (q).
     */
    protected double q;

    /**
     * TODO.
     */
    @Getter(value = AccessLevel.NONE)
    private Map<String, String> baseParameters;

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
     * @throws NullPointerException if any of the arguments are null
     * @throws IllegalArgumentException if {@code S}, {@code K}, {@code T}, or {@code vol} are not greater than zero
     */
    public AbstractOption(
        @NonNull OptionStyle style,
        @NonNull OptionType type,
        @NonNull Number S,
        @NonNull Number K,
        @NonNull Number T, 
        @NonNull Number vol, 
        @NonNull Number r,
        @NonNull Number q
    ) throws IllegalArgumentException, NullPointerException {
        this.checkGreaterThanZero(S, "S");
        this.checkGreaterThanZero(K, "K");
        this.checkGreaterThanZero(T, "T");
        this.checkGreaterThanZero(vol, "vol (σ)");

        this.style = style;
        this.type = type;
        this.S = S.doubleValue();
        this.K = K.doubleValue();
        this.T = T.doubleValue();
        this.vol = vol.doubleValue();
        this.r = r.doubleValue();
        this.q = q.doubleValue();

        this.baseParameters = Map.ofEntries(
            Map.entry(NOTATION_S.trim(), S.toString()),
            Map.entry(NOTATION_K.trim(), K.toString()),
            Map.entry(NOTATION_T.trim(), T.toString()),
            Map.entry(NOTATION_VOL.trim(), vol.toString()),
            Map.entry(NOTATION_R.trim(), r.toString()),
            Map.entry(NOTATION_Q.trim(), q.toString())
        );
    }

    protected Map<String, String> getBaseParameters() {
        return this.baseParameters;
    }

    /**
     * Returns 1 for a call option, -1 for a put option.
     *
     * @return type factor
     */
    protected double typeFactor() {
        return this.type == OptionType.CALL ? 1 : -1;
    }

    /**
     * Returns "C" for a call option, "P" for a put option.
     *
     * @return type parameter
     */
    protected String typeParameterNotation() {
        return this.type == OptionType.CALL ? "C" : "P";
    }

    //region constants
    //----------------------------------------------------------------------

    protected static final String NOTATION_S = " S_0 ";
    protected static final String NOTATION_K = " K ";
    protected static final String NOTATION_T = " T ";
    protected static final String NOTATION_VOL = " \\sigma ";
    protected static final String NOTATION_R = " r ";
    protected static final String NOTATION_Q = " q ";

    //----------------------------------------------------------------------
    //endregion constants

    private void checkGreaterThanZero(Number value, String name) throws IllegalArgumentException {
        if (value.doubleValue() <= 0) {
            throw new IllegalArgumentException(name + " must be greater than zero");
        }
    }
}
