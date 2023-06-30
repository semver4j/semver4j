package org.semver4j;

/**
 * Thrown when something went wrong with parsing semver string.
 */
public class SemverException extends IllegalArgumentException {
    public SemverException(String message) {
        super(message);
    }
}
