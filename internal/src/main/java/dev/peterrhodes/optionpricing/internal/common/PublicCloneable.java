package dev.peterrhodes.optionpricing.internal.common;

/**
 * Generic interface that publicly exposes and types the {@link java.lang.Cloneable} {@code clone} method.
 */
public interface PublicCloneable<T> extends Cloneable {

    /**
     * Clone the object.
     *
     * @see java.lang.Cloneable
     */
    T clone();
}
