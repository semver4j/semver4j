package org.semver4j.range;

import org.semver4j.Semver;
import org.semver4j.processor.CompositeProcessor;
import org.semver4j.processor.Processor;

/**
 * Factory class for creating {@link RangeList} objects used in semantic version range matching.
 *
 * <p>This class provides static factory methods to create {@link RangeList} instances from different sources such as
 * string representations or {@link RangeExpression} objects. The created objects can be used with
 * {@link Semver#satisfies(RangeList)} to check if a version satisfies a given range.
 *
 * <pre>{@code
 * RangesList rangesList = RangesListFactory.create(">=1.0.0 <2.0.0");
 * boolean satisfies = semver.satisfies(rangesList);
 * }</pre>
 */
public class RangeListFactory {
    /** Private constructor to prevent instantiation. */
    private RangeListFactory() {}

    /**
     * Creates a {@link RangeList} from a string representation of version ranges.
     *
     * <p>By default, {@code pre-release} versions are not included in the range matching.
     *
     * @param range the string representation of version ranges (e.g., {@code ">=1.0.0 <2.0.0"})
     * @return a new {@link RangeList} instance
     */
    public static RangeList create(String range) {
        return create(range, false);
    }

    /**
     * Creates a {@link RangeList} from a {@link RangeExpression} object.
     *
     * @param rangeExpressions the ranges expression object
     * @return a new {@link RangeList} instance
     * @since 4.2.0
     */
    public static RangeList create(RangeExpression rangeExpressions) {
        return rangeExpressions.get();
    }

    /**
     * Creates a {@link RangeList} from a string representation of version ranges with control over {@code pre-release}
     * version inclusion.
     *
     * @param range the string representation of version ranges (e.g., {@code ">=1.0.0 <2.0.0"})
     * @param includePreRelease whether to include {@code pre-release} versions in range matching
     * @return a new {@link RangeList} instance
     * @since 5.8.0
     */
    public static RangeList create(String range, boolean includePreRelease) {
        return new RangeExpressionParser().parse(range, includePreRelease);
    }

    /**
     * Creates a {@link RangeList} from a string representation of version ranges with custom processors.
     *
     * <p>By default, {@code pre-release} versions are not included in the range matching.
     *
     * @param range the string representation of version ranges
     * @param processors additional processors to use in sequence
     * @return a new {@link RangeList} instance
     * @since 5.8.0
     */
    public static RangeList create(String range, Processor... processors) {
        return create(range, false, processors);
    }

    /**
     * Creates a {@link RangeList} from a string representation of version ranges with custom processors and control
     * over {@code pre-release} version inclusion.
     *
     * @param range the string representation of version ranges
     * @param includePreRelease whether to include {@code pre-release} versions in range matching
     * @param processors additional processors to use in sequence
     * @return a new {@link RangeList} instance
     * @since 5.8.0
     */
    public static RangeList create(String range, boolean includePreRelease, Processor... processors) {
        return new RangeExpressionParser(CompositeProcessor.of(processors)).parse(range, includePreRelease);
    }
}
