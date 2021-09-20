package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * Base class for all concrete option classes that don't have an analytical solution.&nbsp;If the specific option has an analytical solution then it should extend {@link AbstractAnalyticalOption}.
 */
public abstract class AbstractOption implements Option {

    /**
     * Style of the option, usually defined by the exercise rights, e.g.&nbsp;European, American.
     */
    @Getter @Setter protected OptionStyle style;

    /**
     * Type of the option (call or put).
     */
    @Getter @Setter protected OptionType type;

    /**
     * ({@code S}) Price of the underlying asset.
     */
    @Getter @Setter protected double S;

    /**
     * ({@code K}) Strike/exercise price of the option.
     */
    @Getter @Setter protected double K;

    /**
     * ({@code τ = T - t}) Time to maturity (time from the start of the contract until it expires).
     */
    @Getter @Setter protected double T;

    /**
     * ({@code σ}) Underlying volatility (standard deviation of log returns).
     */
    @Getter @Setter protected double vol;

    /**
     * ({@code r}) Annualized risk-free interest rate, continuously compounded.
     */
    @Getter @Setter protected double r;

    /**
     * ({@code q}) Annual dividend yield, continuously compounded.
     */
    @Getter @Setter protected double q;

    /**
     * TODO.
     */
    private Map<String, String> baseOptionParameters;

    /**
     * Creates an abstract option with the specified parameters.
     *
     * @param style Style of the option defined by the exercise rights, e.g.&nbsp;European, American.
     * @param type Type of the option, i.e.&nbsp;call or put.
     * @param S Price of the underlying asset ({@code S > 0}).
     * @param K Strike/exercise price of the option ({@code K > 0}).
     * @param T Time to maturity/expiration ({@code τ = T - t > 0}).
     * @param vol ({@code σ}) Underlying volatility ({@code σ > 0}).
     * @param r Annualized risk-free interest rate continuously compounded ({@code r}).
     * @param q Annual dividend yield continuously compounded ({@code q}).
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

        this.baseOptionParameters = Map.ofEntries(
            Map.entry(NOTATION_S.trim(), S.toString()),
            Map.entry(NOTATION_K.trim(), K.toString()),
            Map.entry(NOTATION_T.trim(), T.toString()),
            Map.entry(NOTATION_VOL.trim(), vol.toString()),
            Map.entry(NOTATION_R.trim(), r.toString()),
            Map.entry(NOTATION_Q.trim(), q.toString())
        );
    }

    //region getters and setters
    //----------------------------------------------------------------------

    protected Map<String, String> baseOptionParameters() {
        Map<String, String> copy = new HashMap();
        for (Map.Entry<String, String> entry : this.baseOptionParameters.entrySet()) {
            copy.put(entry.getKey(), entry.getValue()); // Strings are immutable
        }
        return copy;
    }

    //----------------------------------------------------------------------
    //endregion constants

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
