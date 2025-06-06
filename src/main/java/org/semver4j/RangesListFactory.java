package org.semver4j;

import org.jspecify.annotations.NullMarked;
import org.semver4j.internal.range.processor.Processor;

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

    /**
     * @since 5.8.0
     */
    public static RangesList create(final String range, Processor start, Processor... additional) {
        return new RangesString(start, additional).get(range);
    }
}
