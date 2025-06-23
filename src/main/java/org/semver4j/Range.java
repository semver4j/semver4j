package org.semver4j;

import static java.lang.String.format;
import static java.util.Arrays.stream;

import java.util.Locale;
import java.util.Objects;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/** Represents single range item. */
@NullMarked
public class Range {
    private final Semver rangeVersion;
    private final RangeOperator rangeOperator;

    public Range(final Semver rangeVersion, final RangeOperator rangeOperator) {
        this.rangeVersion = rangeVersion;
        this.rangeOperator = rangeOperator;
    }

    public Range(final String rangeVersion, final RangeOperator rangeOperator) {
        this(new Semver(rangeVersion), rangeOperator);
    }

    public Semver getRangeVersion() {
        return rangeVersion;
    }

    /**
     * Check whether this range is satisfied by any version.
     *
     * @return {@code true} if this range is satisfied by any version, {@code false} otherwise
     */
    public boolean isSatisfiedByAny() {
        return rangeVersion.isEqualTo(Semver.ZERO) && rangeOperator == RangeOperator.GTE;
    }

    /**
     * Check is range is satisfied by given version.
     *
     * @param version version to check
     * @return {@code true} if range is satisfied by version, {@code false} otherwise
     * @see #isSatisfiedBy(Semver)
     */
    public boolean isSatisfiedBy(final String version) {
        return isSatisfiedBy(new Semver(version));
    }

    /**
     * Check is range is satisfied by given version.
     *
     * @param version version to check
     * @return {@code true} if range is satisfied by version, {@code false} otherwise
     * @see #isSatisfiedBy(String)
     */
    public boolean isSatisfiedBy(final Semver version) {
        return switch (rangeOperator) {
            case EQ -> version.isEquivalentTo(rangeVersion);
            case LT -> version.isLowerThan(rangeVersion);
            case LTE -> version.isLowerThanOrEqualTo(rangeVersion);
            case GT -> version.isGreaterThan(rangeVersion);
            case GTE -> version.isGreaterThanOrEqualTo(rangeVersion);
        };
    }

    @Override
    public boolean equals(final @Nullable Object o) {
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

        RangeOperator(final String string) {
            this.string = string;
        }

        /**
         * String representation of the range operator.
         *
         * @return range operator as string
         */
        public String asString() {
            return string;
        }

        public static RangeOperator value(final String string) {
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
