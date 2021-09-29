package dev.peterrhodes.optionpricing.internal.utils;

import dev.peterrhodes.optionpricing.internal.common.PublicCloneable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Miscellaneous utility methods for copying.
 */
public interface CopyUtils {

    /**
     * Generic deep copy method for an array of objects that implement {@link dev.peterrhodes.optionpricing.common.PublicCloneable}.
     */
    @SuppressWarnings("unchecked")
    static <T extends PublicCloneable<T>> T[] deepCopy(T[] arr, Class<T> c) {
        if (arr == null) {
            return null;
        }

        List<T> list = new ArrayList<T>();
        for (T element : arr) {
            list.add(element.clone());
        }

        T[] deepCopy = (T[]) Array.newInstance(c, list.size());
        return (T[]) list.toArray(deepCopy);
    }

    /**
     * Generic deep copy method for a {@link java.util.List} of objects that implement {@link dev.peterrhodes.optionpricing.common.PublicCloneable}.
     */
    static <T extends PublicCloneable<T>> List<T> deepCopy(List<T> list) {
        if (list == null) {
            return null;
        }

        List<T> deepCopy = new ArrayList<T>();
        for (T element : list) {
            deepCopy.add(element.clone());
        }

        return deepCopy;
    }

    /**
     * Generic deep copy method for a {@link java.util.Map} of key/value pairs where the value implements {@link dev.peterrhodes.optionpricing.common.PublicCloneable}.
     */
    static <K, V extends PublicCloneable<V>> Map<K, V> deepCopy(Map<K, V> map) {
        return map.entrySet().stream()
            .collect(Collectors.toMap(element -> element.getKey(), element -> element.getValue().clone()));
    }

    /**
     * Deep copies a two-dimensional array of {@link java.lang.String}.
     */
    static String[][] deepCopy(String[][] matrix) {
        return Arrays.stream(matrix)
            .map(element -> element.clone())
            .toArray($ -> matrix.clone());
    }
}
