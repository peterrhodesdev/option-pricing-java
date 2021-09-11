package dev.peterrhodes.optionpricing.options;

public class EuropeanOption implements IOption {

    private double T;
    private double r;
    private double S;
    private double σ;
    private double K;

    public EuropeanOption(
        double T,
        double r,
        double S,
        double σ,
        double K
    ) {
        this.T = T;
        this.r = r;
        this.S = S;
        this.σ = σ;
        this.K = K;
    }

    public double analyticalPrice() {
        return 0.0;
    }
}
