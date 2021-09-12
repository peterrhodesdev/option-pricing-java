package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.core.Pricer;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.common.NotImplementedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class CoxRossRubinsteinPricer implements Pricer<CoxRossRubinsteinModel> {

    private int timeSteps;

    /**
     * TODO
     * @throws IllegalArgumentException if timeSteps is not greater than zero
     */
    public CoxRossRubinsteinPricer(int timeSteps) throws IllegalArgumentException {
        if (timeSteps <= 0) {
            throw new IllegalArgumentException("timeSteps must be greater than zero");
        }
        this.timeSteps = timeSteps;
    }

    //region price
    //----------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public double price(Option option) {
        // Determine parameters
        double Δt = option.getT() / (double)(this.timeSteps); // length of a single time interval/step
        double u = Math.exp(option.getV() * Math.sqrt(Δt)); // proportional up movement
        double d = Math.exp(-option.getV() * Math.sqrt(Δt)); // proportional down movement
        double a = Math.exp((option.getR() - option.getQ()) * Δt); // growth factor
        double p = (a - d) / (u - d); // probability of an up movement (probability of a down movement is 1 - p)
        //System.out.println("Δt = " + Δt + ", u = " + u + ", d = " + d + ", p = " + p);

        return this.price(option, Δt, u, d, p);
    }

    private class Node {
        public double S;
        public double V;
        public Node(double S, double V) {
            this.S = S;
            this.V = V;
        }
    }

    private double price(Option option, double Δt, double u, double d, double p) {
        List<Node> nodes = new ArrayList(); // key: ith time step, nth node

        // TODO calc underlying prices
        for (int i = 0; i <= this.timeSteps; i++) { // ith time step: time = iΔt (i = 0, 1, ..., time steps)
            for (int n = 0; n <= i; n++) { // nth node (from lowest underlying price to highest): underlying price = S₀uⁿdⁱ⁻ⁿ
                double S = option.getS() * Math.pow(u, n) * Math.pow(d, i - n);
                //double t = i == this.timeSteps ? option.getT() : i * Δt;
                double V;
                if (i == this.timeSteps) {
                    V = Math.max(0.0, option.getOptionType() == OptionType.CALL ? S - option.getK() : option.getK() - S);
                } else {
                    V = 0.0;
                }
                Node node = new Node(S, V);
                nodes.add(node);
                //System.out.println("i = " + i + ", n = " + n + ": S = " + S);
            }
        }

        // TODO calc option values
        for (int i = this.timeSteps - 1; i >= 0; i--) {
            for (int n = 0; n <= i; n++) {
                int currentIndex = IntStream.rangeClosed(0, i).sum() + n;
                int downIndex = currentIndex + (i + 1);
                int upIndex = downIndex + 1;
                nodes.get(currentIndex).V = (p * nodes.get(upIndex).V + (1 - p) * nodes.get(downIndex).V) * Math.exp(-option.getR() * Δt);
            }
        }

        return nodes.get(0).V;
    }

    //----------------------------------------------------------------------
    //endregion

    /**
     * TODO
     */
    @Override
    public CoxRossRubinsteinModel calculation(Option option) throws NotImplementedException {
        throw new NotImplementedException();
    }
}
