package dev.peterrhodes.optionpricing.utils;

import static org.assertj.core.api.Assertions.assertThat;

import dev.peterrhodes.optionpricing.enums.LatexDelimeterType;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #LatexUtils}.
 */
class LatexUtilsTest {

    @Test
    void Fraction() {
        // Arrange
        String[][] numeratorDenominators = {
            new String[] { "1", "2" },
            new String[] { "1.23", "45.6" },
            new String[] { "a", "b" },
            new String[] { "abc", "xyz" }
        };

        // Act
        List<String> result = new ArrayList<String>();
        for (String[] numeratorDenominator : numeratorDenominators) {
            result.add(LatexUtils.fraction(numeratorDenominator[0], numeratorDenominator[1]));
        }

        // Assert
        String[] expected = {
            "\\frac{1}{2}",
            "\\frac{1.23}{45.6}",
            "\\frac{a}{b}",
            "\\frac{abc}{xyz}"
        };
        this.performAsserts(result, expected);
    }

    @Test
    void Roman_font_style() {
        // Arrange
        String[] lettersArr = { "1", "1.23", "a", "abc" };

        // Act
        List<String> result = new ArrayList<String>();
        for (String letters : lettersArr) {
            result.add(LatexUtils.romanFontStyle(letters));
        }

        // Assert
        String[] expected = {
            "\\mathrm{1}",
            "\\mathrm{1.23}",
            "\\mathrm{a}",
            "\\mathrm{abc}"
        };
        this.performAsserts(result, expected);
    }

    @Test
    void Square_root() {
        // Arrange
        String[] radicands = { "1", "1.23", "a", "abc" };

        // Act
        List<String> result = new ArrayList<String>();
        for (String radicand : radicands) {
            result.add(LatexUtils.squareRoot(radicand));
        }

        // Assert
        String[] expected = {
            "\\sqrt{1}",
            "\\sqrt{1.23}",
            "\\sqrt{a}",
            "\\sqrt{abc}"
        };
        this.performAsserts(result, expected);
    }

    @Test
    void Sub_formula() {
        // Arrange
        String value = "x + y";
        LatexDelimeterType[] latexDelimieterTypes = {
            LatexDelimeterType.PARENTHESIS,
            LatexDelimeterType.BRACKET,
            LatexDelimeterType.BRACE,
            LatexDelimeterType.NONE
        };

        // Act
        List<String> result = new ArrayList<String>();
        for (LatexDelimeterType latexDelimieterType : latexDelimieterTypes) {
            result.add(LatexUtils.subFormula(value, latexDelimieterType));
        }

        // Assert
        String[] expected = {
            "\\left( x + y \\right)",
            "\\left[ x + y \\right]",
            "\\left\\{ x + y \\right\\}",
            "x + y"
        };
        this.performAsserts(result, expected);
    }

    @Test
    void Subscript() {
        // Arrange
        String[][] baseExps = {
            new String[] { "1", "2" },
            new String[] { "1.23", "45.6" },
            new String[] { "a", "b" },
            new String[] { "abc", "xyz" }
        };

        // Act
        List<String> result = new ArrayList<String>();
        for (String[] baseExp : baseExps) {
            result.add(LatexUtils.subscript(baseExp[0], baseExp[1]));
        }

        // Assert
        String[] expected = {
            "1_{2}",
            "1.23_{45.6}",
            "a_{b}",
            "abc_{xyz}"
        };
        this.performAsserts(result, expected);
    }

    @Test
    void Superscript() {
        // Arrange
        String[][] baseExps = {
            new String[] { "1", "2" },
            new String[] { "1.23", "45.6" },
            new String[] { "a", "b" },
            new String[] { "abc", "xyz" }
        };

        // Act
        List<String> result = new ArrayList<String>();
        for (String[] baseExp : baseExps) {
            result.add(LatexUtils.superscript(baseExp[0], baseExp[1]));
        }

        // Assert
        String[] expected = {
            "1^{2}",
            "1.23^{45.6}",
            "a^{b}",
            "abc^{xyz}"
        };
        this.performAsserts(result, expected);
    }

    //region private methods
    //----------------------------------------------------------------------

    private void performAsserts(List<String> result, String[] expected) {
        assertThat(result.size())
            .as("size")
            .isEqualTo(expected.length);

        for (int i = 0; i < expected.length; i++) {
            assertThat(result.get(i))
                .isEqualTo(expected[i]);
        }
    }

    //----------------------------------------------------------------------
    //endregion private methods
}
