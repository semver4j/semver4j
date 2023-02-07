package org.semver4j;

/**
 * Class for create a {@link RangesList} object.
 */
public class RangesListFactory {
    public static RangesList create(String range) {
        return new RangesString().get(range);
    }

    /**
     * @since 4.2.0
     */
    public static RangesList create(RangesExpression rangeExpressions) {
        return rangeExpressions.get();
    }
}
