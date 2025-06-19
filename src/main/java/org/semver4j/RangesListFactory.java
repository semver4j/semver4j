package org.semver4j;

import org.jspecify.annotations.NullMarked;

/**
 * Class for create a {@link RangesList} object.
 */
@NullMarked
public class RangesListFactory {
    /**
     * @since 5.8.0
     */
    public static RangesList create(final String range, boolean includePrerelease) {
        return new RangesString().get(range, includePrerelease);
    }

    public static RangesList create(final String range) {
        return create(range, false);
    }

    /**
     * @since 4.2.0
     */
    public static RangesList create(final RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }
}
