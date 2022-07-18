package org.semver4j;

import java.util.Objects;

import static java.lang.String.format;
import static java.util.Arrays.stream;

public class Range {
    private final Semver rangeVersion;
    private final RangeOperator rangeOperator;

    public Range(Semver rangeVersion, RangeOperator rangeOperator) {
        this.rangeVersion = rangeVersion;
        this.rangeOperator = rangeOperator;
    }

    public Range(String rangeVersion, RangeOperator rangeOperator) {
        this(new Semver(rangeVersion), rangeOperator);
    }

    public Semver getRangeVersion() {
        return rangeVersion;
    }

    public boolean isSatisfiedBy(String version) {
        return isSatisfiedBy(new Semver(version));
    }

    public boolean isSatisfiedBy(Semver version) {
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

        throw new RuntimeException(format("Code error. Unknown RangeOperator: %s", this.rangeOperator));
    }

    @Override
    public boolean equals(Object o) {
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
        /**
         * The version and the requirement are equivalent
         */
        EQ("="),

        /**
         * The version is lower than the requirement
         */
        LT("<"),

        /**
         * The version is lower than or equivalent to the requirement
         */
        LTE("<="),

        /**
         * The version is greater than the requirement
         */
        GT(">"),

        /**
         * The version is greater than or equivalent to the requirement
         */
        GTE(">="),
        ;

        private final String string;

        RangeOperator(String string) {
            this.string = string;
        }

        public String asString() {
            return string;
        }

        public static RangeOperator value(String string) {
            if (string.isEmpty()) {
                return EQ;
            }
            return stream(values())
                    .filter(rangeOperator -> rangeOperator.asString().equals(string))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(format("Range operator for '%s' not found.", string)));
        }
    }
}
