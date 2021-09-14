package dev.peterrhodes.optionpricing.pricers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import dev.peterrhodes.optionpricing.core.LatticeNode;
import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.options.EuropeanOption;
import dev.peterrhodes.optionpricing.options.ExoticOption;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link CoxRossRubinsteinPricer}.
 * References:
 * - Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
 * - Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
 */
class CoxRossRubinsteinPricerTest {

    
    //region helpers
    //----------------------------------------------------------------------

    private final String greaterThanZeroMessage = "must be greater than zero";

    private CoxRossRubinsteinModel createModel(Option option, int timeSteps, double deltat, double u, double d, double p, double price, List<LatticeNode> nodes) {
        CoxRossRubinsteinModel model = new CoxRossRubinsteinModel(option, timeSteps);
        model.setParameters(deltat, u, d, p);
        model.setOutputs(price, nodes);
        return model;
    }

    @SuppressWarnings("checkstyle:multiplevariabledeclarations")
    private void assertCalculation(CoxRossRubinsteinModel result, CoxRossRubinsteinModel expected, double parameterPrecision, double outputPrecision) {
        // parameters
        assertThat(result.getDeltat())
            .as("parameter Δt")
            .isEqualTo(expected.getDeltat(), withPrecision(parameterPrecision));
        assertThat(result.getU())
            .as("parameter u")
            .isEqualTo(expected.getU(), withPrecision(parameterPrecision));
        assertThat(result.getD())
            .as("parameter d")
            .isEqualTo(expected.getD(), withPrecision(parameterPrecision));
        assertThat(result.getP())
            .as("parameter p")
            .isEqualTo(expected.getP(), withPrecision(parameterPrecision));

        // price
        assertThat(result.getPrice()).as("price").isEqualTo(expected.getPrice(), withPrecision(outputPrecision));

        // nodes
        List<LatticeNode> expectedNodes = expected.getNodes();
        List<LatticeNode> resultNodes = result.getNodes();

        // number of nodes
        int size = expectedNodes.size();
        assertThat(resultNodes.size()).as("nodes size").isEqualTo(size);

        for (LatticeNode expectedNode : expectedNodes) {
            // node with same i and n exists
            int i = expectedNode.getI(), j = expectedNode.getJ();
            LatticeNode resultNode = resultNodes.stream()
                .filter(item -> item.getI() == i && item.getJ() == j)
                .findAny()
                .orElse(null);
            assertThat(resultNode)
                .withFailMessage("node (%d, %d) not found", i, j)
                .isNotNull();

            // node(i, n) values
            assertThat(resultNode.getS())
                .as(String.format("node (%d, %d) S", i, j))
                .isEqualTo(expectedNode.getS(), withPrecision(outputPrecision));
            assertThat(resultNode.getV())
                .as(String.format("node (%d, %d) V", i, j))
                .isEqualTo(expectedNode.getV(), withPrecision(outputPrecision));
            assertThat(resultNode.isExercised())
                .as(String.format("node (%d, %d) exercised", i, j))
                .isEqualTo(expectedNode.isExercised());
        }
    }

    //----------------------------------------------------------------------
    //endregion

    //region throws IllegalArgumentException tests
    //----------------------------------------------------------------------

    @Test
    void throwsZeroTimeSteps() {
        // Arrange
        // option: style, type, S, K, T, vol (σ), r, q
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 50, 52, 2, 0.3, 0.05, 0);
        int timeSteps = 0;

        // Act Assert
        assertThatThrownBy(() -> {
            double ex = CoxRossRubinsteinPricer.price(option, timeSteps);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    //----------------------------------------------------------------------
    //endregion

    //region calculation tests, Hull (2014)
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 311, section 13.9, Figure 13.10
     */
    @Test
    void calculationHull2014Fig1310() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 50, 52, 2, 0.3, 0.05, 0);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 50, 7.43, false),
            new LatticeNode(1, 0, 37.04, 14.96, true),
            new LatticeNode(1, 1, 67.49, 0.93, false),
            new LatticeNode(2, 0, 27.44, 24.56, true),
            new LatticeNode(2, 1, 50, 2, true),
            new LatticeNode(2, 2, 91.11, 0, false),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            1, 1.3499, 0.7408, 0.5097, // parameters: deltat (Δt), u, d, p
            7.43, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /**
     * Hull (2014): page 313, section 13.11, Figure 13.11
     */
    @Test
    void calculationHull2014Fig1311() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 810.00, 53.39, false),
            new LatticeNode(1, 0, 732.92, 5.06, false),
            new LatticeNode(1, 1, 895.19, 100.66, false),
            new LatticeNode(2, 0, 663.17, 0, false),
            new LatticeNode(2, 1, 810.00, 10.00, true),
            new LatticeNode(2, 2, 989.34, 189.34, true),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.25, 1.1052, 0.9048, 0.5126, // parameters: deltat (Δt), u, d, p
            53.39, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /**
     * Hull (2014): page 314, section 13.11, Figure 13.12
     */
    @Test
    void calculationHull2014Fig1312() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.CALL, 0.6100, 0.6000, 0.25, 0.12, 0.05, 0.07);
        int timeSteps = 3;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 0.610, 0.019, false),
            new LatticeNode(1, 0, 0.589, 0.007, false),
            new LatticeNode(1, 1, 0.632, 0.033, false),
            new LatticeNode(2, 0, 0.569, 0.000, false),
            new LatticeNode(2, 1, 0.610, 0.015, false),
            new LatticeNode(2, 2, 0.654, 0.054, true),
            new LatticeNode(3, 0, 0.550, 0.000, false),
            new LatticeNode(3, 1, 0.589, 0.000, false),
            new LatticeNode(3, 2, 0.632, 0.032, true),
            new LatticeNode(3, 3, 0.677, 0.077, true),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.0833, 1.0352, 0.9660, 0.4673, // parameters: deltat (Δt), u, d, p
            0.019, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.001); // precision: parameters, outputs
    }

    /**
     * Hull (2014): page 316, section 13.11, Figure 13.13
     */
    @Test
    void calculationHull2014Fig1313() {
        // Arrange
        double r = 0.05;
        double q = r; // Hull (2014), p 315: "in a risk-neutral world a futures price should have an expected growth rate of zero"
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 31, 30, 0.75, 0.3, r, q);
        int timeSteps = 3;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 31.00, 2.84, false),
            new LatticeNode(1, 0, 26.68, 4.54, false),
            new LatticeNode(1, 1, 36.02, 0.93, false),
            new LatticeNode(2, 0, 22.97, 7.03, true),
            new LatticeNode(2, 1, 31.00, 1.76, false),
            new LatticeNode(2, 2, 41.85, 0.00, false),
            new LatticeNode(3, 0, 19.77, 10.23, true),
            new LatticeNode(3, 1, 26.68, 3.32, true),
            new LatticeNode(3, 2, 36.02, 0.00, false),
            new LatticeNode(3, 3, 48.62, 0.00, false),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.2500, 1.1618, 0.8607, 0.4626, // parameters: deltat (Δt), u, d, p
            2.84, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    //----------------------------------------------------------------------
    //endregion

    //region calculation tests, Hull SSM (2014)
    //----------------------------------------------------------------------

    /**
     * Hull SSM (2014): page 142, Problem 13.16
     */
    @Test
    void priceHullSsm2014P1316() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.EUROPEAN, OptionType.CALL, 78, 80, 4 / 12d, 0.3, 0.03, 0);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 78.00, 4.67, false),
            new LatticeNode(1, 0, 69.01, 0.00, false),
            new LatticeNode(1, 1, 88.16, 9.58, false),
            new LatticeNode(2, 0, 61.05, 0.00, false),
            new LatticeNode(2, 1, 78.00, 0.00, false),
            new LatticeNode(2, 2, 99.65, 19.65, true),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.1667, 1.1303, 0.8847, 0.4898, // parameters: deltat (Δt), u, d, p
            4.67, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /**
     * Hull SSM (2014): page 142, Problem 13.17
     */
    @Test
    void priceHullSsm2014P1317() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 1500, 1480, 1, 0.18, 0.04, 0.025);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 1500.00, 78.41, false),
            new LatticeNode(1, 0, 1320.73, 159.27, true),
            new LatticeNode(1, 1, 1703.60, 0.00, false),
            new LatticeNode(2, 0, 1162.89, 317.11, true),
            new LatticeNode(2, 1, 1500.00, 0.00, false),
            new LatticeNode(2, 2, 1934.84, 0.00, false),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.5, 1.1357, 0.8805, 0.4977, // parameters: deltat (Δt), u, d, p
            78.41, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /**
     * Hull SSM (2014): page 143, Problem 13.18a
     */
    @Test
    void priceHullSsm2014P1318a() {
        // Arrange
        double r = 0.03;
        double q = r; // Hull (2014), p 315: "in a risk-neutral world a futures price should have an expected growth rate of zero"
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.CALL, 90, 93, 0.75, 0.28, r, q);
        int timeSteps = 3;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 90.00, 7.94, false),
            new LatticeNode(1, 0, 78.24, 2.24, false),
            new LatticeNode(1, 1, 103.52, 14.62, false),
            new LatticeNode(2, 0, 68.02, 0.00, false),
            new LatticeNode(2, 1, 90.00, 4.86, false),
            new LatticeNode(2, 2, 119.08, 26.08, true),
            new LatticeNode(3, 0, 59.13, 0.00, false),
            new LatticeNode(3, 1, 78.24, 0.00, false),
            new LatticeNode(3, 2, 103.52, 10.52, true),
            new LatticeNode(3, 3, 136.98, 43.98, true),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.25, 1.1503, 0.8694, 0.4651, // parameters: deltat (Δt), u, d, p
            7.94, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /**
     * Hull SSM (2014): page 143, Problem 13.18b
     */
    @Test
    void priceHullSsm2014P1318b() {
        // Arrange
        double r = 0.03;
        double q = r; // Hull (2014), p 315: "in a risk-neutral world a futures price should have an expected growth rate of zero"
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 90, 93, 0.75, 0.28, r, q);
        int timeSteps = 3;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 90.00, 10.88, false),
            new LatticeNode(1, 0, 78.24, 16.88, false),
            new LatticeNode(1, 1, 103.52, 4.16, false),
            new LatticeNode(2, 0, 68.02, 24.98, true),
            new LatticeNode(2, 1, 90.00, 7.84, false),
            new LatticeNode(2, 2, 119.08, 0.00, false),
            new LatticeNode(3, 0, 59.13, 33.87, true),
            new LatticeNode(3, 1, 78.24, 14.76, true),
            new LatticeNode(3, 2, 103.52, 0.00, false),
            new LatticeNode(3, 3, 136.98, 0.00, false),
        });
        CoxRossRubinsteinModel expected = this.createModel(
            option, timeSteps, // inputs
            0.25, 1.1503, 0.8694, 0.4651, // parameters: deltat (Δt), u, d, p
            10.88, expectedNodes // outputs: price, nodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    // TODO Chapter 13 Further Questions

    //----------------------------------------------------------------------
    //endregion

    //region price tests
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //endregion
}
