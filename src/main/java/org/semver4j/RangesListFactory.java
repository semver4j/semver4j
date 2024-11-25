package org.semver4j;

import org.jspecify.annotations.NullMarked;

/**
 * Class for create a {@link RangesList} object.
 */
@NullMarked
public class RangesListFactory {
    public static RangesList create(final String range) {
        return new RangesString().get(range);
    }

    /**
     * @since 4.2.0
     */
    public static RangesList create(final RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }
}
