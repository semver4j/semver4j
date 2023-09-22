package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Integer.parseInt;

/**
 * Set of methods which helps ranges handling.
 */
final class RangesUtils {
    @NotNull
    static final String EMPTY = "";
    @NotNull
    static final String SPACE = " ";

    private static final int X_RANGE_MARKER = -1;

    private RangesUtils() {
    }

    static int parseIntWithXSupport(@Nullable final String id) {
        if (id == null || id.equalsIgnoreCase("x") || id.equals("*") || id.equals("+")) {
            return X_RANGE_MARKER;
        }
        return parseInt(id);
    }

    static boolean isX(int id) {
        return id == X_RANGE_MARKER;
    }

    static boolean isNotBlank(@Nullable final String id) {
        return id != null && !id.isEmpty();
    }
}
