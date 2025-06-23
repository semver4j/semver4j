package org.semver4j.processor;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;

import java.util.Locale;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.semver4j.Semver;

/** Set of methods which helps ranges handling. */
@NullMarked
final class RangesUtils {
    static final String EMPTY = "";
    static final String SPACE = " ";
    static final String ALL_RANGE = format(Locale.ROOT, "%s%s", GTE.asString(), Semver.ZERO);
    static final String ALL_RANGE_WITH_PRERELEASE =
            format(Locale.ROOT, "%s%s%s", GTE.asString(), Semver.ZERO, Processor.LOWEST_PRERELEASE);

    private static final int X_RANGE_MARKER = -1;

    private RangesUtils() {}

    static int parseIntWithXSupport(final @Nullable String id) {
        if (id == null || id.equalsIgnoreCase("x") || id.equals("*") || id.equals("+")) {
            return X_RANGE_MARKER;
        }
        return parseInt(id);
    }

    static boolean isX(int id) {
        return id == X_RANGE_MARKER;
    }

    static boolean isNotBlank(final @Nullable String id) {
        return id != null && !id.isEmpty();
    }
}
