package dev.peterrhodes.optionpricing.pricers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import dev.peterrhodes.optionpricing.AnalyticOptionFactory;
import dev.peterrhodes.optionpricing.Option;
import dev.peterrhodes.optionpricing.OptionBuilder;
import dev.peterrhodes.optionpricing.common.LatticeNode;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #CoxRossRubinsteinPricer}.
 * <ul>
 *   <li>Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.</li>
 *   <li>Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.</li>
 * </ul>
 */
@SuppressWarnings("checkstyle:multiplevariabledeclarations")
class CoxRossRubinsteinPricerTest {

    //region throws IllegalArgumentException tests
    //----------------------------------------------------------------------

    @Test
    void Zero_time_steps_should_throw() {
        // Arrange
        // option: S, K, τ, σ, r, q
        Option option = new OptionBuilder(50, 52, 2, 0.3, 0.05, 0)
            .europeanStyle()
            .asPut()
            .build();
        int timeSteps = 0;

        // Act Assert
        assertThatThrownBy(() -> {
            CoxRossRubinsteinPricer ex = new CoxRossRubinsteinPricer(option.contract(), timeSteps);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("must be greater than zero");
    }

    //----------------------------------------------------------------------
    //endregion

    //region calculation tests
    //----------------------------------------------------------------------

    /**
     * Hull (2014): page 314, section 13.11, Figure 13.12.
     */
    @Test
    void American_call_calculation_Hull2014Fig1312() {
        // Arrange
        Option option = new OptionBuilder(0.6100, 0.6000, 0.25, 0.12, 0.05, 0.07)
            .americanStyle()
            .asCall()
            .build();
        int timeSteps = 3;
        CoxRossRubinsteinPricer pricer = new CoxRossRubinsteinPricer(option.contract(), timeSteps);

        // Act
        CoxRossRubinsteinModel result = pricer.calculation();

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
        CoxRossRubinsteinModel expected = new CoxRossRubinsteinModel(
            0.019, timeSteps,
            0.0833, 1.0352, 0.9660, 0.4673, // parameters: deltat (Δt), u, d, p
            expectedNodes
        );

        this.assertCalculation(result, expected, 0.0001, 0.001); // precision: parameters, outputs
    }

    /**
     * Hull SSM (2014): page 142, Problem 13.17.
     */
    @Test
    void American_put_calculation_HullSsm2014P1317() {
        // Arrange
        Option option = new OptionBuilder(1500, 1480, 1, 0.18, 0.04, 0.025)
            .americanStyle()
            .asPut()
            .build();
        int timeSteps = 2;
        CoxRossRubinsteinPricer pricer = new CoxRossRubinsteinPricer(option.contract(), timeSteps);

        // Act
        CoxRossRubinsteinModel result = pricer.calculation();

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
        CoxRossRubinsteinModel expected = new CoxRossRubinsteinModel(78.41, timeSteps, 0.5, 1.1357, 0.8805, 0.4977, expectedNodes);

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /**
     * Hull (2014): page 313, section 13.11, Figure 13.11.
     */
    @Test
    void European_call_calculation_Hull2014Fig1311() {
        // Arrange
        Option option = new OptionBuilder(810, 800, 0.5, 0.2, 0.05, 0.02)
            .europeanStyle()
            .asCall()
            .build();
        int timeSteps = 2;
        CoxRossRubinsteinPricer pricer = new CoxRossRubinsteinPricer(option.contract(), timeSteps);

        // Act
        CoxRossRubinsteinModel result = pricer.calculation();

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
        CoxRossRubinsteinModel expected = new CoxRossRubinsteinModel(53.39, timeSteps, 0.25, 1.1052, 0.9048, 0.5126, expectedNodes);

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
        assertThat(result.getPrice())
            .as("same result for concrete implementation")
            .isEqualTo(AnalyticOptionFactory.createEuropeanCall(810, 800, 0.5, 0.2, 0.05, 0.02).coxRossRubinsteinPrice(timeSteps));
    }

    /**
     * Hull SSM (2014): page 144, Problem 13.19.
     */
    @Test
    void European_put_calculation_HullSsm2014P1319() {
        // Arrange
        Option option = new OptionBuilder(140, 150, 0.5, 0.25, 0.04, 0)
            .europeanStyle()
            .asPut()
            .build();
        int timeSteps = 2;
        CoxRossRubinsteinPricer pricer = new CoxRossRubinsteinPricer(option.contract(), timeSteps);

        // Act
        CoxRossRubinsteinModel result = pricer.calculation();

        // Assert
        List<LatticeNode> expectedNodes = Arrays.asList(new LatticeNode[] {
            // node: i, n, S, V, exercised
            new LatticeNode(0, 0, 140.00, 14.58, false),
            new LatticeNode(1, 0, 123.55, 24.96, false),
            new LatticeNode(1, 1, 158.64, 4.86, false),
            new LatticeNode(2, 0, 109.03, 40.97, true),
            new LatticeNode(2, 1, 140.00, 10.00, true),
            new LatticeNode(2, 2, 179.76, 0.00, false),
        });
        CoxRossRubinsteinModel expected = new CoxRossRubinsteinModel(14.58, timeSteps, 0.25, 1.1331, 0.8825, 0.5089, expectedNodes);

        this.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
        assertThat(result.getPrice())
            .as("same result for concrete implementation")
            .isEqualTo(AnalyticOptionFactory.createEuropeanPut(140, 150, 0.5, 0.25, 0.04, 0).coxRossRubinsteinPrice(timeSteps));
    }

    //----------------------------------------------------------------------
    //endregion
    
    //region private methods
    //----------------------------------------------------------------------

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
}
