package org.semver4j;

import org.jspecify.annotations.NullMarked;

/**
 * Class for create a {@link RangesList} object.
 */
@NullMarked
public class RangesListFactory {
    public static RangesList create(final String range) {
        return create(range, false);
    }

    public static RangesList create(final String range, boolean includePrerelease) {
        return new RangesString().get(range, includePrerelease);
    }

    /**
     * @since 4.2.0
     */
    public static RangesList create(final RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }
}
