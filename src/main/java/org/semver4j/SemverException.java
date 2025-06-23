package org.semver4j;

import org.jspecify.annotations.NullMarked;

/** Thrown when something went wrong with parsing semver string. */
@NullMarked
public class SemverException extends IllegalArgumentException {
    public SemverException(String message) {
        super(message);
    }
}
