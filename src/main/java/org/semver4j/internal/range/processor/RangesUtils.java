package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.semver4j.Semver;

import java.util.Locale;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;

/**
 * Set of methods which helps ranges handling.
 */
final class RangesUtils {
    @NotNull
    static final String EMPTY = "";
    @NotNull
    static final String SPACE = " ";
    @NotNull
    static final String ASTERISK = "*";
    @NotNull
    static final String ALL_RANGE = format(Locale.ROOT, "%s%s", GTE.asString(), Semver.ZERO);

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
