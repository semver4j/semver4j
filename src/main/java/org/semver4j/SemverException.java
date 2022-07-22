package org.semver4j;

/**
 * General exception when something went wrong with semver actions.
 */
public class SemverException extends RuntimeException {
    public SemverException(String message) {
        super(message);
    }
}
