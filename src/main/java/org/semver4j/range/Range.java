package org.semver4j.range;

import static java.lang.String.format;
import static java.util.Arrays.stream;

import java.util.Locale;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.semver4j.Semver;

/**
 * Represents a single version range constraint used for semantic version matching.
 *
 * <p>A range consists of a version and an operator (such as {@code >=}, {@code <}, etc.) that defines the relationship
 * between the provided version and versions that satisfy the range. For example, the range {@code >=1.2.3} is satisfied
 * by any version that is greater than or equal to 1.2.3.
 *
 * <p>Ranges can be combined into more complex expressions through {@link RangeList} to create version constraints like
 * {@code >=1.0.0 <2.0.0} (meaning: any version at least 1.0.0 but less than 2.0.0).
 *
 * @see RangeList For handling multiple range constraints
 * @see RangeListFactory For creating range lists from string expressions
 */
public class Range {
    private final Semver rangeVersion;
    private final RangeOperator rangeOperator;

    /**
     * Constructs a new range constraint with the specified version and operator.
     *
     * @param rangeVersion the version to compare against
     * @param rangeOperator the operator defining the comparison relationship
     */
    public Range(Semver rangeVersion, RangeOperator rangeOperator) {
        this.rangeVersion = rangeVersion;
        this.rangeOperator = rangeOperator;
    }

    /**
     * Constructs a new range constraint with the specified version string and operator.
     *
     * @param rangeVersion the version string to compare against (will be converted to a {@link Semver} object)
     * @param rangeOperator the operator defining the comparison relationship
     */
    public Range(String rangeVersion, RangeOperator rangeOperator) {
        this(new Semver(rangeVersion), rangeOperator);
    }

    /**
     * Returns the operator used in this range constraint.
     *
     * @return the operator component of this range.
     */
    public RangeOperator getRangeOperator() {
        return rangeOperator;
    }

    /**
     * Returns the version used in this range constraint.
     *
     * @return the version component of this range
     */
    public Semver getRangeVersion() {
        return rangeVersion;
    }

    /**
     * Checks whether this range is satisfied by any version.
     *
     * <p>A range is satisfied by any version if it's {@code >=0.0.0}.
     *
     * @return {@code true} if this range is satisfied by any version, {@code false} otherwise
     */
    public boolean isSatisfiedByAny() {
        return rangeVersion.isEqualTo(Semver.ZERO) && rangeOperator == RangeOperator.GTE;
    }

    /**
     * Checks if this range is satisfied by the given version string.
     *
     * <p>The version string will be converted to a {@link Semver} object before comparison.
     *
     * @param version version string to check
     * @return {@code true} if the range is satisfied by the version, {@code false} otherwise
     * @see #isSatisfiedBy(Semver)
     */
    public boolean isSatisfiedBy(String version) {
        return isSatisfiedBy(new Semver(version));
    }

    /**
     * Checks if this range is satisfied by the given version.
     *
     * <p>The comparison is performed according to the range operator:
     *
     * <ul>
     *   <li>{@code =} - version must be equivalent to the range version
     *   <li>{@code <} - version must be lower than the range version
     *   <li>{@code <=} - version must be lower than or equal to the range version
     *   <li>{@code >} - version must be greater than the range version
     *   <li>{@code >=} - version must be greater than or equal to the range version
     * </ul>
     *
     * @param version version to check
     * @return {@code true} if the range is satisfied by the version, {@code false} otherwise
     * @see #isSatisfiedBy(String)
     */
    public boolean isSatisfiedBy(Semver version) {
        return switch (rangeOperator) {
            case EQ -> version.isEquivalentTo(rangeVersion);
            case LT -> version.isLowerThan(rangeVersion);
            case LTE -> version.isLowerThanOrEqualTo(rangeVersion);
            case GT -> version.isGreaterThan(rangeVersion);
            case GTE -> version.isGreaterThanOrEqualTo(rangeVersion);
        };
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Range range = (Range) o;
        return Objects.equals(rangeVersion, range.rangeVersion) && rangeOperator == range.rangeOperator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rangeVersion, rangeOperator);
    }

    /**
     * Returns a string representation of this range.
     *
     * <p>The format is the operator followed by the version, for example: {@code ">=1.2.3"}.
     *
     * @return string representation of this range
     */
    @Override
    public String toString() {
        return rangeOperator.asString() + rangeVersion;
    }

    public enum RangeOperator {
        /** The version and the requirement are equivalent. */
        EQ("="),

        /** The version is lower than the requirement. */
        LT("<"),

        /** The version is lower than or equivalent to the requirement. */
        LTE("<="),

        /** The version is greater than the requirement. */
        GT(">"),

        /** The version is greater than or equivalent to the requirement. */
        GTE(">="),
        ;

        private final String string;

        /**
         * Constructs a range operator with the specified string representation.
         *
         * @param string the string representation of this operator
         */
        RangeOperator(String string) {
            this.string = string;
        }

        /**
         * Returns the string representation of this range operator.
         *
         * <p>For example, the {@code GTE} operator returns {@code ">="}.
         *
         * @return range operator as string
         */
        public String asString() {
            return string;
        }

        /**
         * Returns the range operator corresponding to the given string.
         *
         * <p>An empty string is interpreted as the equality operator ({@code =}).
         *
         * @param string the string representation of a range operator
         * @return the corresponding range operator
         * @throws IllegalArgumentException if no matching operator is found
         */
        public static RangeOperator value(String string) {
            if (string.isEmpty()) {
                return EQ;
            }
            return stream(values())
                    .filter(rangeOperator -> rangeOperator.asString().equals(string))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(
                            format(Locale.ROOT, "Range operator for '%s' not found", string)));
        }
    }
}
