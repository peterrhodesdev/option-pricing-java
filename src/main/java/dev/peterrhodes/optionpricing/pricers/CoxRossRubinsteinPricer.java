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
        checkParameters(timeSteps);
        CoxRossRubinsteinModel model = performCalculation(option, timeSteps);
        return model.getPrice();
    }

    //region calculation
    //----------------------------------------------------------------------

    /**
     * TODO
     */
    public static CoxRossRubinsteinModel calculation(Option option, int timeSteps) {
        checkParameters(timeSteps);
        return performCalculation(option, timeSteps);
    }

    private static CoxRossRubinsteinModel performCalculation(Option option, int timeSteps) {
        CoxRossRubinsteinModel model = determineModelParameters(option, timeSteps);
        double Δt = model.getDeltat(), u = model.getU(), d = model.getD(), p = model.getP();

        List<CoxRossRubinsteinModel.Node> nodes = new ArrayList();

        // Work forwards creating the tree
        for (int i = 0; i <= timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int n = 0; n <= i; n++) { // nth node (from lowest underlying price to highest): underlying price = S₀uⁿdⁱ⁻ⁿ
                CoxRossRubinsteinModel.Node node = createNode(option, i, n, u, d, timeSteps);
                nodes.add(node);
            }
        }

        // Work backwards calculating the option values
        for (int i = timeSteps; i >= 0; i--) {
            for (int n = 0; n <= i; n++) {
                calculateNodeOptionValue(option, timeSteps, nodes, i, n, Δt, p);
            }
        }

        model.setOutputs(nodes.get(0).getV(), nodes);
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

    private static CoxRossRubinsteinModel.Node createNode(Option option, int i, int n, double u, double d, int timeSteps) {
        double S = option.getS() * Math.pow(u, n) * Math.pow(d, i - n);
        //double t = i == this.timeSteps ? option.getT() : i * Δt;
        double V = 0; // Can't calculate yet

        CoxRossRubinsteinModel.Node node = new CoxRossRubinsteinModel.Node(i, n, S, V, false);
        return node;
    }

    private static void calculateNodeOptionValue(Option option, int timeSteps, List<CoxRossRubinsteinModel.Node> nodes, int i, int n, double Δt, double p) {
        int currentIndex = IntStream.rangeClosed(0, i).sum() + n;
        CoxRossRubinsteinModel.Node currentNode = nodes.get(currentIndex);
        double S = currentNode.getS();

        double V;

        if (i == timeSteps) {
            double exerciseValue = calculateOptionExerciseValue(option, S);
            V = Math.max(0.0, exerciseValue);
            currentNode.setExercised(exerciseValue > 0);
        } else {
            int downIndex = currentIndex + (i + 1);
            int upIndex = downIndex + 1;
            double optionCurrentValue = (p * nodes.get(upIndex).getV() + (1 - p) * nodes.get(downIndex).getV()) * Math.exp(-option.getR() * Δt);
            double earlyExerciseValue;
            switch (option.getStyle()) {
                case AMERICAN:
                    earlyExerciseValue = Math.max(0.0, calculateOptionExerciseValue(option, S));
                    break;
                case EUROPEAN:
                default:
                    earlyExerciseValue = 0.0;
            }
            V = Math.max(optionCurrentValue, earlyExerciseValue);
            currentNode.setExercised(earlyExerciseValue > optionCurrentValue);
        }

        currentNode.setV(V);
    }

    private static double calculateOptionExerciseValue(Option option, double S) {
        return option.getType() == OptionType.CALL ? S - option.getK() : option.getK() - S;
    }

    //----------------------------------------------------------------------
    //endregion
}
