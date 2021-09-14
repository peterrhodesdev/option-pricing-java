package dev.peterrhodes.optionpricing.common;

/**
 * Custom exception class used for code that hasn't been implemented yet.
 *
 * @see <a href="https://stackoverflow.com/a/50461711/4545255">stackoverflow answer</a>
 */
public class NotYetImplementedException extends RuntimeException {

    /**
     * Throw this for code that still needs to be implemented.
     *
     * @deprecated Deprecated to serve as a reminder to implement the corresponding code.
     */
    @Deprecated(forRemoval = false)
    public NotYetImplementedException() {}
}
