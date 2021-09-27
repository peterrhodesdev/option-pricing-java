package dev.peterrhodes.optionpricing.internal.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Miscellaneous math utility methods.
 */
public interface MathUtils {

    /**
     * Big decimal representation of pi to 50 decimal places.
     */
    BigDecimal PI = new BigDecimal("3.14159265358979323846264338327950288419716939937510");

    /**
     * Calculates the factorial (!) of a non-negative integer.
     *
     * @param n non-negative integer
     * @return {@code n!}
     * @throws IllegalArgumentException if {@code n < 0}
     */
    static BigInteger factorial(int n) throws IllegalArgumentException {
        if (n < 0) {
            throw new IllegalArgumentException("n can't be less than zero");
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }

        return result;
    }

    /**
     * Returns the (Gauss) error function ({@code erf}) evaluated at {@code x}.
     *
     * @param x point to evaluate the error function at
     * @return error function at {@code x}
     */
    static double erf(Number x) {
        if (x.doubleValue() == 0d) {
            return 0d;
        } else if (Math.abs(x.doubleValue()) > 3.5) {
            // erf(3.5) = 0.999999257
            return 1d;
        }

        BigInteger two = new BigInteger("2");
        MathContext mathContext = MathContext.DECIMAL128;
        int maxIteration = 50; // x = 3.5 requires 48 iterations with a minTerm of 10^-10
        BigDecimal minTerm = BigDecimal.TEN.pow(-10, mathContext);

        // Taylor series expansion
        BigDecimal absX = new BigDecimal(x.toString()).abs();
        BigDecimal result = absX;
        for (int n = 1; n <= maxIteration; n++) {
            BigDecimal numerator = absX.pow(2 * n + 1);
            BigDecimal denominator = new BigDecimal(factorial(n).multiply(two.multiply(BigInteger.valueOf(n)).add(BigInteger.ONE)));
            BigDecimal term = numerator.divide(denominator, mathContext);

            if (n % 2 == 0) {
                result = result.add(term);
            } else {
                result = result.subtract(term);
            }

            if (term.compareTo(minTerm) == -1) {
                break;
            }
        }

        result = result.multiply(new BigDecimal(two)).divide(PI.sqrt(mathContext), mathContext);

        return x.doubleValue() > 0d ? result.doubleValue() : -result.doubleValue();
    }

    /**
     * Returns the standard normal cumulative distribution function (CDF) evaluated at {@code x}.
     *
     * @param x point to evaluate the standard normal CDF at
     * @return standard normal CDF at {@code x}
     */
    static double standardNormalCdf(double x) {
        return 0.5 * (1d + erf(x / Math.sqrt(2)));
    }

    /**
     * Returns the standard normal probability density function (PDF) evaluated at {@code x}.
     *
     * @param x point to evaluate the standard normal PDF at
     * @return standard normal PDF at {@code x}
     */
    static double standardNormalPdf(double x) {
        return Math.exp(-x * x / 2d) / (Math.sqrt(2d * Math.PI));
    }
}
