package dev.peterrhodes.optionpricing.core;

import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for concrete option classes that have an analytical solution, e.g.&nbsp;vanilla European options.&nbsp;If the specific option doesn't have an analytical solution then it should extend {@link AbstractOption}.
 */
public abstract class AbstractAnalyticalOption extends AbstractOption implements AnalyticalOption {

    /**
     * Creates an abstract analytical option with the specified parameters.
     *
     * @param style {@link AbstractOption#style}
     * @param type {@link AbstractOption#type}
     * @param S {@link AbstractOption#S}
     * @param K {@link AbstractOption#K}
     * @param T {@link AbstractOption#T}
     * @param vol {@link AbstractOption#vol}
     * @param r {@link AbstractOption#r}
     * @param q {@link AbstractOption#q}
     * @throws IllegalArgumentException from {@link AbstractOption#AbstractOption(OptionStyle, OptionType, double, double, double, double, double, double)}
     */
    public AbstractAnalyticalOption(OptionStyle style, OptionType type, double S, double K, double T, double vol, double r, double q) throws IllegalArgumentException {
        super(style, type, S, K, T, vol, r, q);
    }

    protected final List<EquationInput> baseCalculationInputs() {
        List<EquationInput> inputs = new ArrayList();

        inputs.add(new EquationInput(NOTATION_S, Double.toString(this.S)));
        inputs.add(new EquationInput(NOTATION_K, Double.toString(this.K)));
        inputs.add(new EquationInput(NOTATION_T, Double.toString(this.T)));
        inputs.add(new EquationInput(NOTATION_VOL, Double.toString(this.vol)));
        inputs.add(new EquationInput(NOTATION_R, Double.toString(this.r)));
        inputs.add(new EquationInput(NOTATION_Q, Double.toString(this.q)));

        return inputs;
    }

    protected static final List<Parameter> baseFunctions() {
        List<Parameter> functions = new ArrayList();

        // Parameter for the standard normal cumulative distribution function.
        // Using N instead of Φ (Greek uppercase letter phi, LaTeX = "\Phi") to denote the function.
        Parameter standardNormalCdf = new Parameter(
            notationStandardNormalCdf("x") + " = \\frac{1}{\\sqrt{2\\pi}} \\int_{-\\infty}^{x} e^{-\\frac{z^2}{2}} dz",
            "standard normal cumulative distribution function"
        );
        functions.add(standardNormalCdf);

        // Parameter for the standard normal probability density function.
        // Using N' instead of φ (Greek lowercase letter phi variant, LaTeX = "\varphi") to denote the function.
        Parameter standardNormalPdf = new Parameter(
            "\\mathrm{N}' (x) = \\frac{d{\\mathrm{N} (x) (x)}}{dx} = \\frac{1}{\\sqrt{2\\pi}} e^{-\\frac{x^2}{2}}",
            "standard normal probability density function"
        );
        functions.add(standardNormalPdf);

        return functions;
    }

    protected static final String notationStandardNormalCdf(String argument) {
        return "\\mathrm{N} (" + argument + ")";
    }
}
