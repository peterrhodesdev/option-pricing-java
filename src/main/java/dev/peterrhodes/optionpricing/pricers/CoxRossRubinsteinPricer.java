package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public abstract class CoxRossRubinsteinPricer {

    private static void checkParameters(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
    }

    /**
     * TODO
     * @throws IllegalArgumentException if timeSteps is not greater than zero
     */
    public static double price(Option option, int timeSteps) throws IllegalArgumentException {
        CoxRossRubinsteinPricer.checkParameters(timeSteps);
        CoxRossRubinsteinModel model = CoxRossRubinsteinPricer.performCalculation(option, timeSteps);
        return model.getPrice();
    }

    //region calculation
    //----------------------------------------------------------------------

    /**
     * TODO
     */
    public static CoxRossRubinsteinModel calculation(Option option, int timeSteps) {
        CoxRossRubinsteinPricer.checkParameters(timeSteps);
        return CoxRossRubinsteinPricer.performCalculation(option, timeSteps);
    }

    private static CoxRossRubinsteinModel performCalculation(Option option, int timeSteps) {
        CoxRossRubinsteinModel model = CoxRossRubinsteinPricer.determineModelParameters(option, timeSteps);
        double Δt = model.getDeltat(), u = model.getU(), d = model.getD(), p = model.getP();

        List<CoxRossRubinsteinModel.Node> nodes = new ArrayList();

        // TODO calc underlying prices
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int n = 0; n <= i; n++) { // nth node (from lowest underlying price to highest): underlying price = S₀uⁿdⁱ⁻ⁿ
                double S = option.getS() * Math.pow(u, n) * Math.pow(d, i - n);
                //double t = i == this.timeSteps ? option.getT() : i * Δt;
                double V;
                if (i == timeSteps) {
                    V = Math.max(0.0, option.getType() == OptionType.CALL ? S - option.getK() : option.getK() - S);
                } else {
                    switch (option.getStyle()) {
                        case EUROPEAN:
                            V = 0.0;
                            break;
                        case AMERICAN:
                            // Payoff from early exercise
                            V = Math.max(0.0, option.getType() == OptionType.CALL ? S - option.getK() : option.getK() - S);
                            break;
                        default:
                            V = 0.0;
                    }
                }
                CoxRossRubinsteinModel.Node node = new CoxRossRubinsteinModel.Node(i, n, S, V);
                nodes.add(node);
                //System.out.println("i = " + i + ", n = " + n + ": S = " + S);
            }
        }

        // TODO calc option values
        for (int i = timeSteps - 1; i >= 0; i--) {
            for (int n = 0; n <= i; n++) {
                int currentIndex = IntStream.rangeClosed(0, i).sum() + n;
                int downIndex = currentIndex + (i + 1);
                int upIndex = downIndex + 1;
                double optionCurrentValue = (p * nodes.get(upIndex).V + (1 - p) * nodes.get(downIndex).V) * Math.exp(-option.getR() * Δt);
                nodes.get(currentIndex).V = Math.max(nodes.get(currentIndex).V, optionCurrentValue);
            }
        }

        model.setOutputs(nodes.get(0).V, nodes);

        return model;
    }

    private static CoxRossRubinsteinModel determineModelParameters(Option option, int timeSteps) {
        CoxRossRubinsteinModel model = new CoxRossRubinsteinModel(option, timeSteps);
        
        double Δt = option.getT() / timeSteps; // length of a single time interval/step
        double u = Math.exp(option.getV() * Math.sqrt(Δt)); // proportional up movement
        double d = Math.exp(-option.getV() * Math.sqrt(Δt)); // proportional down movement
        double a = Math.exp((option.getR() - option.getQ()) * Δt); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)

        model.setParameters(Δt, u, d, p);

        return model;
    }

    //----------------------------------------------------------------------
    //endregion
}
