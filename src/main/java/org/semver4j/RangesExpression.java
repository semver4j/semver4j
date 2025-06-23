package org.semver4j;

import static org.semver4j.Range.RangeOperator.*;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NullMarked;

/**
 * The internal expression class used to create a ranges.<br>
 * Allows to create ranges using a fluent interface.
 *
 * <p>Usage:
 *
 * <pre>
 * equal("1.0.0")
 *     .and(less("2.0.0"))
 *     .or(greaterOrEqual("3.0.0"))
 * </pre>
 *
 * <p>Will produce range:
 *
 * <pre>
 * (=1.0.0 and &lt;2.0.0) or &gt;=3.0.0
 * </pre>
 *
 * @since 4.2.0
 */
@NullMarked
@SuppressWarnings("checkstyle:DeclarationOrder")
public class RangesExpression {
    private final RangesList rangesList = new RangesList(false);

    private final List<Range> andOperationRanges = new ArrayList<>();

    /**
     * Expression for equal range item.
     *
     * @param version should be a valid semver string
     */
    public static RangesExpression equal(final String version) {
        return equal(new Semver(version));
    }

    /** Expression for equal range item. */
    public static RangesExpression equal(final Semver version) {
        return new RangesExpression(new Range(version, EQ));
    }

    /**
     * Expression for greater range item.
     *
     * @param version should be a valid semver string
     */
    public static RangesExpression greater(final String version) {
        return greater(new Semver(version));
    }

    /** Expression for greater range item. */
    public static RangesExpression greater(final Semver version) {
        return new RangesExpression(new Range(version, GT));
    }

    /**
     * Expression for greater or equal range item.
     *
     * @param version should be a valid semver string
     */
    public static RangesExpression greaterOrEqual(final String version) {
        return greaterOrEqual(new Semver(version));
    }

    /** Expression for greater or equal range item. */
    public static RangesExpression greaterOrEqual(final Semver version) {
        return new RangesExpression(new Range(version, GTE));
    }

    /**
     * Expression for less range item.
     *
     * @param version should be a valid semver string
     */
    public static RangesExpression less(final String version) {
        return less(new Semver(version));
    }

    /** Expression for lee range item. */
    public static RangesExpression less(final Semver version) {
        return new RangesExpression(new Range(version, LT));
    }

    /**
     * Expression for less or equal range item.
     *
     * @param version should be a valid semver string
     */
    public static RangesExpression lessOrEqual(final String version) {
        return lessOrEqual(new Semver(version));
    }

    /** Expression for less or equal range item. */
    public static RangesExpression lessOrEqual(final Semver version) {
        return new RangesExpression(new Range(version, LTE));
    }

    RangesExpression(final Range range) {
        andOperationRanges.add(range);
    }

    /** Allows to join ranges using {@code AND} operator. */
    public RangesExpression and(final RangesExpression rangeExpression) {
        RangesList rangesList = rangeExpression.get();
        List<List<Range>> lists = rangesList.get();
        for (List<Range> list : lists) {
            andOperationRanges.addAll(list);
            if (lists.size() > 1) {
                flushAndClearAndOperationRangesToRangesList();
            }
        }
        return this;
    }

    /** Allows to join ranges using {@code OR} operator. */
    public RangesExpression or(final RangesExpression rangeExpression) {
        flushAndClearAndOperationRangesToRangesList();
        return and(rangeExpression);
    }

    RangesList get() {
        if (!andOperationRanges.isEmpty()) {
            flushAndClearAndOperationRangesToRangesList();
        }
        return rangesList;
    }

    private void flushAndClearAndOperationRangesToRangesList() {
        rangesList.add(new ArrayList<>(andOperationRanges));
        andOperationRanges.clear();
    }
}
