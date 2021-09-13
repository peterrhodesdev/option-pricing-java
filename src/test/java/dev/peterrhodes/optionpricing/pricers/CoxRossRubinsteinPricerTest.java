package dev.peterrhodes.optionpricing.pricers;

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

    //region helpers
    //----------------------------------------------------------------------

    private final String greaterThanZeroMessage = "must be greater than zero";

    private static void assertCalculation(CoxRossRubinsteinModel result, double price, List<CoxRossRubinsteinModel.Node> expectedNodes, double precision) {
        assertThat(result.getPrice()).isEqualTo(price, withPrecision(precision));

        List<CoxRossRubinsteinModel.Node> resultNodes = result.getNodes();

        int size = expectedNodes.size();
        assertThat(resultNodes.size()).isEqualTo(size);

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

            assertThat(resultNode.getS()).isEqualTo(expectedNode.getS(), withPrecision(precision));
            assertThat(resultNode.getV()).isEqualTo(expectedNode.getV(), withPrecision(precision));
        }
    }
    //----------------------------------------------------------------------
    //endregion

    //region IllegalArgumentException tests
    //----------------------------------------------------------------------

    @Test
    void zeroTimeSteps() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);

        // Act Assert
        assertThatThrownBy(() -> {
            double ex = CoxRossRubinsteinPricer.price(option, 0);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining(this.greaterThanZeroMessage);
    }

    //----------------------------------------------------------------------
    //endregion

    //region calculation tests
    //----------------------------------------------------------------------

    /*
     * Hull: page 311, section 13.9, Figure 13.10
     */
    @Test
    void calculation_Hull_Fig13_10() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.PUT, 50, 52, 2, 0.3, 0.05, 0);

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, 2);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            new CoxRossRubinsteinModel.Node(0, 0, 50, 7.43),
            new CoxRossRubinsteinModel.Node(1, 0, 37.04, 14.96),
            new CoxRossRubinsteinModel.Node(1, 1, 67.49, 0.93),
            new CoxRossRubinsteinModel.Node(2, 0, 27.44, 24.56),
            new CoxRossRubinsteinModel.Node(2, 1, 50, 2),
            new CoxRossRubinsteinModel.Node(2, 2, 91.11, 0),
        });
        CoxRossRubinsteinPricerTest.assertCalculation(result, 7.43, expectedNodes, 0.01);
    }

    /*
     * Hull: page 313, section 13.11, Example 13.1
     */
    @Test
    void calculation_Hull_Ex13_1() {
        // Arrange
        EuropeanOption option = new EuropeanOption(OptionType.CALL, 810, 800, 0.5, 0.2, 0.05, 0.02);

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, 2);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
            new CoxRossRubinsteinModel.Node(0, 0, 810.00, 53.39),
            new CoxRossRubinsteinModel.Node(1, 0, 732.92, 5.06),
            new CoxRossRubinsteinModel.Node(1, 1, 895.19, 100.66),
            new CoxRossRubinsteinModel.Node(2, 0, 663.17, 0),
            new CoxRossRubinsteinModel.Node(2, 1, 810.00, 10.00),
            new CoxRossRubinsteinModel.Node(2, 2, 989.34, 189.34),
        });
        CoxRossRubinsteinPricerTest.assertCalculation(result, 53.39, expectedNodes, 0.01);
    }

    /*
     * Hull: page 314, section 13.11, Example 13.2
     */
    @Test
    void calculation_Hull_Ex13_2() {
        // Arrange
        ExoticOption option = new ExoticOption(OptionStyle.AMERICAN, OptionType.CALL, 0.6100, 0.6000, 0.25, 0.12, 0.05, 0.07);

        // Act
        CoxRossRubinsteinModel result = CoxRossRubinsteinPricer.calculation(option, 3);

        // Assert
        List<CoxRossRubinsteinModel.Node> expectedNodes = Arrays.asList(new CoxRossRubinsteinModel.Node[] {
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
        CoxRossRubinsteinPricerTest.assertCalculation(result, 0.019, expectedNodes, 0.001);
    }

    //----------------------------------------------------------------------
    //endregion
}
