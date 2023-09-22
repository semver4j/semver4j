package org.semver4j;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.stream;

/**
 * Represents single range item.
 */
public class Range {
    @NotNull
    private final Semver rangeVersion;
    @NotNull
    private final RangeOperator rangeOperator;

    public Range(@NotNull final Semver rangeVersion, @NotNull final RangeOperator rangeOperator) {
        this.rangeVersion = rangeVersion;
        this.rangeOperator = rangeOperator;
    }

    public Range(@NotNull final String rangeVersion, @NotNull final RangeOperator rangeOperator) {
        this(new Semver(rangeVersion), rangeOperator);
    }

    @NotNull
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
    public boolean isSatisfiedBy(@NotNull final String version) {
        return isSatisfiedBy(new Semver(version));
    }

    /**
     * Check is range is satisfied by given version.
     *
     * @param version version to check
     * @return {@code true} if range is satisfied by version, {@code false} otherwise
     * @see #isSatisfiedBy(String)
     */
    public boolean isSatisfiedBy(@NotNull final Semver version) {
        switch (rangeOperator) {
            case EQ:
                return version.isEquivalentTo(rangeVersion);
            case LT:
                return version.isLowerThan(rangeVersion);
            case LTE:
                return version.isLowerThanOrEqualTo(rangeVersion);
            case GT:
                return version.isGreaterThan(rangeVersion);
            case GTE:
                return version.isGreaterThanOrEqualTo(rangeVersion);
        }

        throw new RuntimeException(format(Locale.ROOT, "Unknown RangeOperator: %s", rangeOperator));
    }

    @Override
    public boolean equals(@Nullable final Object o) {
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
    @NotNull
    public String toString() {
        return rangeOperator.asString() + rangeVersion;
    }

    public enum RangeOperator {
        /**
         * The version and the requirement are equivalent.
         */
        EQ("="),

        /**
         * The version is lower than the requirement.
         */
        LT("<"),

        /**
         * The version is lower than or equivalent to the requirement.
         */
        LTE("<="),

        /**
         * The version is greater than the requirement.
         */
        GT(">"),

        /**
         * The version is greater than or equivalent to the requirement.
         */
        GTE(">="),
        ;

        @NotNull
        private final String string;

        RangeOperator(@NotNull final String string) {
            this.string = string;
        }

        /**
         * String representation of the range operator.
         *
         * @return range operator as string
         */
        @NotNull
        public String asString() {
            return string;
        }

        @NotNull
        public static RangeOperator value(@NotNull final String string) {
            if (string.isEmpty()) {
                return EQ;
            }
            return stream(values())
                .filter(rangeOperator -> rangeOperator.asString().equals(string))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(format(Locale.ROOT, "Range operator for '%s' not found", string)));
        }
    }
}
