package dev.peterrhodes.optionpricing.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #CopyUtils}.
 */
class CopyUtilsTest {

    @Test
    void Deep_copy_matrix_of_strings() {
        // Arrange
        String[][] originalArray = {{"a", "a"}, {"a", "a"}};

        // Act
        String[][] result = CopyUtils.deepCopy(originalArray);
        originalArray[0][0] = "z";

        // Assert
        assertThat(result[0][0]).isEqualTo("a");
    }
}
