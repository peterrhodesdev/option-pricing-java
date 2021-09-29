package dev.peterrhodes.optionpricing.internal.utils;

import static org.assertj.core.api.Assertions.assertThat;

import dev.peterrhodes.optionpricing.internal.common.PublicCloneable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Test class for {@link #CopyUtils}.
 */
public class CopyUtilsTest {

    //region List tests
    //----------------------------------------------------------------------

    @Test
    public void Deep_copy_null_list_should_return_null() {
        // Arrange
        List<TestClass> original = null;

        // Act
        List<TestClass> deepCopy = CopyUtils.deepCopy(original);

        // Assert
        assertThat(deepCopy).isNull();
    }

    @Test
    public void Deep_copy_empty_list_should_return_empty_list() {
        // Arrange
        List<TestClass> original = new ArrayList<TestClass>();

        // Act
        List<TestClass> deepCopy = CopyUtils.deepCopy(original);

        // Assert
        assertThat(deepCopy).isNotNull();
        assertThat(deepCopy.size()).as("size").isEqualTo(0);
    }

    @Test
    public void Deep_copy_list_of_objects() {
        // Arrange
        List<TestClass> original = new ArrayList<TestClass>();
        original.add(new TestClass("a"));
        original.add(new TestClass("b"));
        List<TestClass> shallowCopy = original;

        // Act
        List<TestClass> deepCopy = CopyUtils.deepCopy(original);
        original.get(0).setField("z");

        // Assert
        assertThat(shallowCopy.get(0).getField()).as("shallow copy").isEqualTo("z");
        assertThat(deepCopy.get(0).getField()).as("deep copy").isEqualTo("a");
    }

    //endregion List tests
    //----------------------------------------------------------------------

    //region array tests
    //----------------------------------------------------------------------

    @Test
    public void Deep_copy_null_array_should_return_null() {
        // Arrange
        TestClass[] original = null;

        // Act
        TestClass[] deepCopy = CopyUtils.deepCopy(original, TestClass.class);

        // Assert
        assertThat(deepCopy).isNull();
    }

    @Test
    public void Deep_copy_empty_array_should_return_empty_array() {
        // Arrange
        TestClass[] original = {};

        // Act
        TestClass[] deepCopy = CopyUtils.deepCopy(original, TestClass.class);

        // Assert
        assertThat(deepCopy).isNotNull();
        assertThat(deepCopy.length).as("length").isEqualTo(0);
    }

    @Test
    public void Deep_copy_array_of_objects() {
        // Arrange
        TestClass[] original = { new TestClass("a"), new TestClass("b") };
        TestClass[] shallowCopy = original;

        // Act
        TestClass[] deepCopy = CopyUtils.deepCopy(original, TestClass.class);
        original[0].setField("z");

        // Assert
        assertThat(shallowCopy[0].getField()).as("shallow copy").isEqualTo("z");
        assertThat(deepCopy[0].getField()).as("deep copy").isEqualTo("a");
    }

    //endregion array tests
    //----------------------------------------------------------------------

    @Test
    public void Deep_copy_map_with_object_values() {
        // Arrange
        Map<Integer, TestClass> original = new HashMap<Integer, TestClass>();
        original.put(0, new TestClass("a"));
        original.put(1, new TestClass("b"));
        Map<Integer, TestClass> shallowCopy = original;

        // Act
        Map<Integer, TestClass> deepCopy = CopyUtils.deepCopy(original);
        original.get(0).setField("z");

        // Assert
        assertThat(shallowCopy.get(0).getField()).as("shallow copy").isEqualTo("z");
        assertThat(deepCopy.get(0).getField()).as("deep copy").isEqualTo("a");
    }

    @Test
    public void Deep_copy_matrix_of_strings() {
        // Arrange
        String[][] original = {{"a", "b"}, {"c", "d"}};
        String[][] shallowCopy = original;

        // Act
        String[][] deepCopy = CopyUtils.deepCopy(original);
        original[0][0] = "z";

        // Assert
        assertThat(shallowCopy[0][0]).as("shallow copy").isEqualTo("z");
        assertThat(deepCopy[0][0]).as("deep copy").isEqualTo("a");
    }

    private static class TestClass implements PublicCloneable<TestClass> {

        private String field;

        public TestClass(String field) {
            this.field = field;
        }

        public String getField() {
            return this.field;
        }

        public void setField(String field) {
            this.field = field;
        }

        @Override
        public TestClass clone() {
            try {
                return (TestClass) super.clone();
            } catch (CloneNotSupportedException e) {
                return new TestClass(this.field);
            }
        }
    }
}
