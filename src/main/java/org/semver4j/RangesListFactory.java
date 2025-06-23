package org.semver4j;

import org.jspecify.annotations.NullMarked;
import org.semver4j.processor.Processor;

/** Class for create a {@link RangesList} object. */
@NullMarked
public class RangesListFactory {
    /** @since 5.8.0 */
    public static RangesList create(final String range, boolean includePrerelease) {
        return new RangesString().get(range, includePrerelease);
    }

    public static RangesList create(final String range) {
        return create(range, false);
    }

    /** @since 4.2.0 */
    public static RangesList create(final RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }

    /** @since 5.8.0 */
    public static RangesList create(final String range, Processor start, Processor... additional) {
        return new RangesString(start, additional).get(range, false);
    }

    /** @since 5.8.0 */
    public static RangesList create(
            final String range, boolean includePrerelease, Processor start, Processor... additional) {
        return new RangesString(start, additional).get(range, includePrerelease);
    }
}
