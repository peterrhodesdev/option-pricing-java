package dev.peterrhodes.optionpricing.internal.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.withPrecision;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #MathUtils}.
 */
public class MathUtilsTest {

    @Test
    public void Factorial_less_than_zero_should_throw() {
        // Arrange
        int n = -1;

        // Act Assert
        assertThatThrownBy(() -> {
            BigInteger ex = MathUtils.factorial(n);
        })
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("less than zero");
    }

    @Test
    public void Factorial() {
        // Arrange
        int[] nValues = { 0, 1, 2, 3, 4, 10, 20, 21, 22 };

        // Act
        List<BigInteger> results = new ArrayList<BigInteger>();
        for (int n : nValues) {
            results.add(MathUtils.factorial(n));
        }

        // Assert
        // https://oeis.org/A000142/list
        String[] expected = { "1", "1", "2", "6", "24", "3628800", "2432902008176640000", "51090942171709440000", "1124000727777607680000" };
        for (int i = 0; i < expected.length; i++) {
            assertThat(results.get(i).toString())
                .as(String.format("n = %d", nValues[i]))
                .isEqualTo(expected[i]);
        }
    }

    @Test
    public void Erf() {
        // Arrange
        double[] xValues = {
            0,
            0.02,
            0.9,
            1,
            3.5,
            3.51
        };

        // Act
        List<Double> results = new ArrayList<Double>();
        for (double x : xValues) {
            results.add(MathUtils.erf(x));
        }

        // Assert
        double[] expected = {
            0d,
            0.022564575,
            0.796908212,
            0.842700793,
            0.999999257,
            1d
        };

        for (int i = 0; i < expected.length; i++) {
            assertThat(results.get(i))
                .as(String.format("x = %f", xValues[i]))
                .isEqualTo(expected[i], withPrecision(0.000000001));
        }

        assertThat(MathUtils.erf(-0.9))
            .as("erf(-x) = -erf(x)")
            .isEqualTo(-0.796908212, withPrecision(0.000000001));
    }
}
