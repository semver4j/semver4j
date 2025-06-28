package org.semver4j.range;

import static org.semver4j.range.Range.RangeOperator.*;

import java.util.ArrayList;
import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.semver4j.Semver;
import org.semver4j.SemverException;

/**
 * A fluent builder for creating semantic version range expressions.
 *
 * <p>This class provides a type-safe, readable way to construct complex version range expressions through method
 * chaining. It converts the fluent expressions into a {@link RangeList} that can be used for version matching.
 *
 * <p>Range expressions are composed of individual range constraints that can be combined with logical operators:
 *
 * <ul>
 *   <li>{@code and} - requires that a version satisfies both conditions
 *   <li>{@code or} - requires that a version satisfies at least one condition
 * </ul>
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * // Create a range expression: (=1.0.0 and <2.0.0) or >=3.0.0
 * RangeExpression expression = RangeExpression.eq("1.0.0")
 *     .and(RangeExpression.less("2.0.0"))
 *     .or(RangeExpression.greaterOrEqual("3.0.0"));
 *
 * // Use the expression to check if a version satisfies it
 * boolean satisfies = semver.satisfies(expression);
 * }</pre>
 *
 * <p>This is equivalent to the string range expression: {@code "=1.0.0 <2.0.0 || >=3.0.0"}
 *
 * @see RangeList For the underlying structure that evaluates version matches
 * @see Range For individual version constraints
 * @since 4.2.0
 */
@NullMarked
public class RangeExpression {
    private final RangeList rangeList = new RangeList(false);

    private final List<Range> andOperationRanges = new ArrayList<>();

    /**
     * Creates a range expression that matches versions equal to the specified version.
     *
     * @param version a valid semantic version string
     * @return a new range expression that matches versions equal to the specified version
     * @throws SemverException if the version string is not a valid semantic version
     */
    public static RangeExpression eq(String version) {
        return eq(new Semver(version));
    }

    /**
     * Creates a range expression that matches versions equal to the specified version.
     *
     * @param version a semantic version instance
     * @return a new range expression that matches versions equal to the specified version
     */
    public static RangeExpression eq(Semver version) {
        return new RangeExpression(new Range(version, EQ));
    }

    /**
     * Creates a range expression that matches versions greater than the specified version.
     *
     * @param version a valid semantic version string
     * @return a new range expression that matches versions greater than the specified version
     * @throws SemverException if the version string is not a valid semantic version
     */
    public static RangeExpression greater(String version) {
        return greater(new Semver(version));
    }

    /**
     * Creates a range expression that matches versions greater than the specified version.
     *
     * @param version a semantic version instance
     * @return a new range expression that matches versions greater than the specified version
     */
    public static RangeExpression greater(Semver version) {
        return new RangeExpression(new Range(version, GT));
    }

    /**
     * Creates a range expression that matches versions greater than or equal to the specified version.
     *
     * @param version a valid semantic version string
     * @return a new range expression that matches versions greater than or equal to the specified version
     * @throws SemverException if the version string is not a valid semantic version
     */
    public static RangeExpression greaterOrEqual(String version) {
        return greaterOrEqual(new Semver(version));
    }

    /**
     * Creates a range expression that matches versions greater than or equal to the specified version.
     *
     * @param version a semantic version instance
     * @return a new range expression that matches versions greater than or equal to the specified version
     */
    public static RangeExpression greaterOrEqual(final Semver version) {
        return new RangeExpression(new Range(version, GTE));
    }

    /**
     * Creates a range expression that matches versions less than the specified version.
     *
     * @param version a valid semantic version string
     * @return a new range expression that matches versions less than the specified version
     * @throws SemverException if the version string is not a valid semantic version
     */
    public static RangeExpression less(String version) {
        return less(new Semver(version));
    }

    /**
     * Creates a range expression that matches versions less than the specified version.
     *
     * @param version a semantic version instance
     * @return a new range expression that matches versions less than the specified version
     */
    public static RangeExpression less(Semver version) {
        return new RangeExpression(new Range(version, LT));
    }

    /**
     * Creates a range expression that matches versions less than or equal to the specified version.
     *
     * @param version a semantic version instance
     * @return a new range expression that matches versions less than or equal to the specified version
     */
    public static RangeExpression lessOrEqual(String version) {
        return lessOrEqual(new Semver(version));
    }

    /**
     * Creates a range expression that matches versions less than or equal to the specified version.
     *
     * @param version a semantic version instance
     * @return a new range expression that matches versions less than or equal to the specified version
     */
    public static RangeExpression lessOrEqual(Semver version) {
        return new RangeExpression(new Range(version, LTE));
    }

    /**
     * Creates a new range expression with the specified range constraint.
     *
     * @param range the initial range constraint for this expression
     */
    private RangeExpression(Range range) {
        andOperationRanges.add(range);
    }

    /**
     * Combines this range expression with another using a logical AND operator.
     *
     * <p>The resulting expression will only match versions that satisfy both this expression and the provided
     * expression.
     *
     * <p>For example, {@code eq("1.0.0").and(less("2.0.0"))} will match versions that are both equal to "1.0.0" and
     * less than "2.0.0" (which effectively means just "1.0.0").
     *
     * @param rangeExpression the expression to combine with this one using AND logic
     * @return this expression instance for method chaining
     */
    public RangeExpression and(RangeExpression rangeExpression) {
        RangeList ranges = rangeExpression.get();
        List<List<Range>> lists = ranges.get();
        for (List<Range> list : lists) {
            andOperationRanges.addAll(list);
            if (lists.size() > 1) {
                flushAndClearAndOperationRangesToRangesList();
            }
        }
        return this;
    }

    /**
     * Combines this range expression with another using a logical OR operator.
     *
     * <p>The resulting expression will match versions that satisfy either this expression or the provided expression.
     *
     * <p>For example, {@code eq("1.0.0").or(eq("2.0.0"))} will match versions that are either equal to "1.0.0" or equal
     * to "2.0.0".
     *
     * @param rangeExpression the expression to combine with this one using OR logic
     * @return this expression instance for method chaining
     */
    public RangeExpression or(RangeExpression rangeExpression) {
        flushAndClearAndOperationRangesToRangesList();
        return and(rangeExpression);
    }

    /**
     * Returns the {@link RangeList} representation of this expression.
     *
     * <p>This method is primarily used internally by the library and by
     * {@link RangeListFactory#create(RangeExpression)}.
     *
     * @return a {@link RangeList} representing this expression
     */
    RangeList get() {
        if (!andOperationRanges.isEmpty()) {
            flushAndClearAndOperationRangesToRangesList();
        }
        return rangeList;
    }

    private void flushAndClearAndOperationRangesToRangesList() {
        rangeList.add(new ArrayList<>(andOperationRanges));
        andOperationRanges.clear();
    }
}
