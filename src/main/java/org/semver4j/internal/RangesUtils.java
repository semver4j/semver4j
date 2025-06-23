package org.semver4j.internal;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.processor.Processor.LOWEST_PRERELEASE;

import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.semver4j.Semver;

/**
 * Utility methods for handling semantic versioning ranges. Provides functionality for parsing, comparing, and
 * manipulating version ranges.
 */
public class RangesUtils {
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String ALL_RANGE = format(Locale.ROOT, "%s%s", GTE.asString(), Semver.ZERO);
    public static final String ALL_RANGE_WITH_PRERELEASE = GTE.asString() + Semver.ZERO + LOWEST_PRERELEASE;

    /** Marker value used to represent an {@code x}, {@code *}, or {@code +} in version ranges. */
    private static final int X_RANGE_MARKER = -1;

    /** Private constructor to prevent instantiation of utility class. */
    private RangesUtils() {}

    /**
     * Parses a string to an integer with support for {@code x}, {@code *}, and {@code +} wildcards.
     *
     * @param id the string to parse, may be {@code null}
     * @return the parsed integer value, or {@link #X_RANGE_MARKER} if the input is a wildcard
     */
    public static int parseIntWithXSupport(@Nullable String id) {
        if (id == null || id.equalsIgnoreCase("x") || id.equals("*") || id.equals("+")) {
            return X_RANGE_MARKER;
        }
        return parseInt(id);
    }

    /**
     * Checks if the given integer is a wildcard marker.
     *
     * @param id the integer to check
     * @return {@code true} if the integer is a wildcard marker, {@code false} otherwise
     */
    public static boolean isX(int id) {
        return id == X_RANGE_MARKER;
    }

    /**
     * Checks if the given string is not null and not empty.
     *
     * @param id the string to check, may be {@code null}
     * @return {@code true} if the string is not null and not empty, {@code false} otherwise
     */
    public static boolean isNotBlank(@Nullable String id) {
        return id != null && !id.isEmpty();
    }
}
