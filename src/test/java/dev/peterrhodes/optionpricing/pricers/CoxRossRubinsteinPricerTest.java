package dev.peterrhodes.optionpricing.pricers;

import dev.peterrhodes.optionpricing.core.Option;
import dev.peterrhodes.optionpricing.enums.OptionStyle;
import dev.peterrhodes.optionpricing.enums.OptionType;
import dev.peterrhodes.optionpricing.models.CoxRossRubinsteinModel;
import dev.peterrhodes.optionpricing.options.EuropeanOption;
import dev.peterrhodes.optionpricing.options.ExoticOption;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

class CoxRossRubinsteinPricerTest {

    /*
     * References:
     * - Hull (2014): Hull, J. (2014) Options, Futures and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
     * - Hull SSM (2014): Hull, J. (2014) Student Solutions Manual for Options, Futures, and Other Derivatives. 9th Edition, Prentice Hall, Upper Saddle River.
     */
    
    //region helpers
    //----------------------------------------------------------------------

    private final String greaterThanZeroMessage = "must be greater than zero";

    private static CoxRossRubinsteinModel createModel(Option option, int timeSteps, double Δt, double u, double d, double p, double price, List<CoxRossRubinsteinModel.Node> nodes) {
        CoxRossRubinsteinModel model = new CoxRossRubinsteinModel(option, timeSteps);
        model.setParameters(Δt, u, d, p);
        model.setOutputs(price, nodes);
        return model;
    }

    private static void assertCalculation(CoxRossRubinsteinModel result, CoxRossRubinsteinModel expected, double parameterPrecision, double outputPrecision) {
        // parameters
        assertThat(result.getDeltat()).as("parameter Δt").isEqualTo(expected.getDeltat(), withPrecision(parameterPrecision));
        assertThat(result.getU()).as("parameter u").isEqualTo(expected.getU(), withPrecision(parameterPrecision));
        assertThat(result.getD()).as("parameter d").isEqualTo(expected.getD(), withPrecision(parameterPrecision));
        assertThat(result.getP()).as("parameter p").isEqualTo(expected.getP(), withPrecision(parameterPrecision));

        // price
        assertThat(result.getPrice()).as("price").isEqualTo(expected.getPrice(), withPrecision(outputPrecision));

        // nodes
        List<CoxRossRubinsteinModel.Node> expectedNodes = expected.getNodes();
        List<CoxRossRubinsteinModel.Node> resultNodes = result.getNodes();

        int size = expectedNodes.size();
        assertThat(resultNodes.size()).as("nodes size").isEqualTo(size);

        for (CoxRossRubinsteinModel.Node expectedNode : expectedNodes) {
            int i = expectedNode.getI(), n = expectedNode.getN();
            CoxRossRubinsteinModel.Node resultNode = resultNodes.stream()
                .filter(item -> item.getI() == i && item.getN() == n)
                .findAny()
                .orElse(null);
            assertThat(resultNode)
                .withFailMessage("node (%d, %d) not found", i, n)
                .isNotNull();

            /*System.out.println("expectedNode");
            System.out.println("i = " + expectedNode.getI() + ", n = " + expectedNode.getN() + ", S = " + expectedNode.getS() + ", V = " + expectedNode.getV());
            System.out.println("resultNode");
            System.out.println("i = " + resultNode.getI() + ", n = " + resultNode.getN() + ", S = " + resultNode.getS() + ", V = " + resultNode.getV());*/

            assertThat(resultNode.getS()).as(String.format("node (%d, %d) S", i, n)).isEqualTo(expectedNode.getS(), withPrecision(outputPrecision));
            assertThat(resultNode.getV()).as(String.format("node (%d, %d) V", i, n)).isEqualTo(expectedNode.getV(), withPrecision(outputPrecision));
        }
    }

    //----------------------------------------------------------------------
    //endregion

    //region throws IllegalArgumentException tests
    //----------------------------------------------------------------------

    @Test
    void throws_zeroTimeSteps() {
        // Arrange
        // option: style, type, S, K, T, v (σ), r, q
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

    //region calculation tests
    //----------------------------------------------------------------------

    /*
     * Hull (2014): page 311, section 13.9, Figure 13.10
     */
    @Test
    void calculation_Hull2014_Fig13_10() {
        // Arrange
        // option: style, type, S, K, T, v (σ), r, q
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 50, 52, 2, 0.3, 0.05, 0);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            // node: i, n, S, V
            new CoxRossRubinsteinModel.Node(0, 0, 50, 7.43),
            new CoxRossRubinsteinModel.Node(1, 0, 37.04, 14.96),
            new CoxRossRubinsteinModel.Node(1, 1, 67.49, 0.93),
            new CoxRossRubinsteinModel.Node(2, 0, 27.44, 24.56),
            new CoxRossRubinsteinModel.Node(2, 1, 50, 2),
            new CoxRossRubinsteinModel.Node(2, 2, 91.11, 0),
        });
        CoxRossRubinsteinModel expected = CoxRossRubinsteinPricerTest.createModel(
            option, timeSteps, // inputs
            1, 1.3499, 0.7408, 0.5097, // parameters: deltat (Δt), u, d, p
            7.43, expectedNodes // outputs: price, nodes
        );

        CoxRossRubinsteinPricerTest.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /*
     * Hull (2014): page 313, section 13.11, Figure 13.11
     */
    @Test
    void calculation_Hull2014_Fig13_11() {
        // Arrange
        // European option: type, S, K, T, v (σ), r, q
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            // node: i, n, S, V
            new CoxRossRubinsteinModel.Node(0, 0, 810.00, 53.39),
            new CoxRossRubinsteinModel.Node(1, 0, 732.92, 5.06),
            new CoxRossRubinsteinModel.Node(1, 1, 895.19, 100.66),
            new CoxRossRubinsteinModel.Node(2, 0, 663.17, 0),
            new CoxRossRubinsteinModel.Node(2, 1, 810.00, 10.00),
            new CoxRossRubinsteinModel.Node(2, 2, 989.34, 189.34),
        });
        CoxRossRubinsteinModel expected = CoxRossRubinsteinPricerTest.createModel(
            option, timeSteps, // inputs
            0.25, 1.1052, 0.9048, 0.5126, // parameters: deltat (Δt), u, d, p
            53.39, expectedNodes // outputs: price, nodes
        );

        CoxRossRubinsteinPricerTest.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /*
     * Hull (2014): page 314, section 13.11, Figure 13.12
     */
    @Test
    void calculation_Hull2014_Fig13_12() {
        // Arrange
        // option: style, type, S, K, T, v (σ), r, q
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.CALL, 0.6100, 0.6000, 0.25, 0.12, 0.05, 0.07);
        int timeSteps = 3;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            // node: i, n, S, V
            new CoxRossRubinsteinModel.Node(0, 0, 0.610, 0.019),
            new CoxRossRubinsteinModel.Node(1, 0, 0.589, 0.007),
            new CoxRossRubinsteinModel.Node(1, 1, 0.632, 0.033),
            new CoxRossRubinsteinModel.Node(2, 0, 0.569, 0.000),
            new CoxRossRubinsteinModel.Node(2, 1, 0.610, 0.015),
            new CoxRossRubinsteinModel.Node(2, 2, 0.654, 0.054),
            new CoxRossRubinsteinModel.Node(3, 0, 0.550, 0.000),
            new CoxRossRubinsteinModel.Node(3, 1, 0.589, 0.000),
            new CoxRossRubinsteinModel.Node(3, 2, 0.632, 0.032),
            new CoxRossRubinsteinModel.Node(3, 3, 0.677, 0.077),
        });
        CoxRossRubinsteinModel expected = CoxRossRubinsteinPricerTest.createModel(
            option, timeSteps, // inputs
            0.0833, 1.0352, 0.9660, 0.4673, // parameters: deltat (Δt), u, d, p
            0.019, expectedNodes // outputs: price, nodes
        );

        CoxRossRubinsteinPricerTest.assertCalculation(result, expected, 0.0001, 0.001); // precision: parameters, outputs
    }

    /*
     * Hull (2014): page 316, section 13.11, Figure 13.13
     */
    @Test
    void calculation_Hull2014_Fig13_13() {
        // Arrange
        // option: style, type, S, K, T, v (σ), r, q
        double r = 0.05;
        double q = r; // "in a risk-neutral world a futures price should have an expected growth rate of zero"
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 31, 30, 0.75, 0.3, r, q);
        int timeSteps = 3;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            // node: i, n, S, V
            new CoxRossRubinsteinModel.Node(0, 0, 31.00, 2.84),
            new CoxRossRubinsteinModel.Node(1, 0, 26.68, 4.54),
            new CoxRossRubinsteinModel.Node(1, 1, 36.02, 0.93),
            new CoxRossRubinsteinModel.Node(2, 0, 22.97, 7.03),
            new CoxRossRubinsteinModel.Node(2, 1, 31.00, 1.76),
            new CoxRossRubinsteinModel.Node(2, 2, 41.85, 0.00),
            new CoxRossRubinsteinModel.Node(3, 0, 19.77, 10.23),
            new CoxRossRubinsteinModel.Node(3, 1, 26.68, 3.32),
            new CoxRossRubinsteinModel.Node(3, 2, 36.02, 0.00),
            new CoxRossRubinsteinModel.Node(3, 3, 48.62, 0.00),
        });
        CoxRossRubinsteinModel expected = CoxRossRubinsteinPricerTest.createModel(
            option, timeSteps, // inputs
            0.2500, 1.1618, 0.8607, 0.4626, // parameters: deltat (Δt), u, d, p
            2.84, expectedNodes // outputs: price, nodes
        );

        CoxRossRubinsteinPricerTest.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    /*
     * Hull SSM (2014): page 142, Problem 13.16
     */
    @Test
    void price_HullSSM2014_P13_16() {
        // Arrange
        // option: style, type, S, K, T, v (σ), r, q
        ExoticOption option = new ExoticOption(OptionStyle.EUROPEAN, OptionType.CALL, 78, 80, 4/12d, 0.3, 0.03, 0);
        int timeSteps = 2;

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, timeSteps);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            // node: i, n, S, V
            new CoxRossRubinsteinModel.Node(0, 0, 78.00, 4.67),
            new CoxRossRubinsteinModel.Node(1, 0, 69.01, 0.00),
            new CoxRossRubinsteinModel.Node(1, 1, 88.16, 9.58),
            new CoxRossRubinsteinModel.Node(2, 0, 61.05, 0.00),
            new CoxRossRubinsteinModel.Node(2, 1, 78.00, 0.00),
            new CoxRossRubinsteinModel.Node(2, 2, 99.65, 19.65),
        });
        CoxRossRubinsteinModel expected = CoxRossRubinsteinPricerTest.createModel(
            option, timeSteps, // inputs
            0.1667, 1.1303, 0.8847, 0.4898, // parameters: deltat (Δt), u, d, p
            4.67, expectedNodes // outputs: price, nodes
        );

        CoxRossRubinsteinPricerTest.assertCalculation(result, expected, 0.0001, 0.01); // precision: parameters, outputs
    }

    //----------------------------------------------------------------------
    //endregion

    //region price tests
    //----------------------------------------------------------------------


    //----------------------------------------------------------------------
    //endregion
}
