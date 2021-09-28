package dev.peterrhodes.optionpricing.models;

/**
 * Parameters that are required to determine the exercise value of an option.
 */
public class ExerciseValueInput {

    private double time;
    private double spotPrice;

    private ExerciseValueInput(Builder builder) {
        this.time = builder.time;
        this.spotPrice = builder.spotPrice;
    }

    /**
     * Builder class.
     */
    public static class Builder {
        private final double time;
        private final double spotPrice;

        /**
         * Builds an ExerciseValueInput object.
         *
         * @param time current time ({@code t})
         * @param spotPrice current underlying asset price ({@code Sₜ})
         */
        public Builder(double time, double spotPrice) {
            this.time = time;
            this.spotPrice = spotPrice;
        }

        /**
         * Builds the object.
         *
         * @return the new object
         */
        public ExerciseValueInput build() {
            return new ExerciseValueInput(this);
        }
    }

    //region getters
    //----------------------------------------------------------------------

    /**
     * Get time.
     *
     * @return time
     */
    public double getTime() {
        return this.time;
    }

    /**
     * Get spotPrice.
     *
     * @return spotPrice
     */
    public double getSpotPrice() {
        return this.spotPrice;
    }

    //----------------------------------------------------------------------
    //endregion getters
}
