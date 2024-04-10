package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;

import java.util.List;

import static java.lang.Math.max;

public class Comparator implements Comparable<Semver> {
    private static final String ALL_DIGITS = "^\\d+$";
    private static final String CONTAINS_DIGITS = ".*\\d.*";
    private static final String TRAILING_DIGITS_EXTRACT = "(?<=\\D)(?=\\d)";
    private static final String LEADING_DIGITS_EXTRACT = "(?<=\\d)(?=\\D)";

    @NotNull
    private static final String UNDEFINED_MARKER = "undef";

    @NotNull
    private final Semver version;

    public Comparator(@NotNull final Semver version) {
        this.version = version;
    }

    @Override
    public int compareTo(@NotNull final Semver other) {
        int result = mainCompare(other);
        if (result == 0) {
            return preReleaseCompare(other);
        }
        return result;
    }

    private int mainCompare(@NotNull final Semver other) {
        int majorCompare = Long.compare(version.getMajor(), other.getMajor());
        if (majorCompare == 0) {
            int minorCompare = Long.compare(version.getMinor(), other.getMinor());
            if (minorCompare == 0) {
                return Long.compare(version.getPatch(), other.getPatch());
            } else {
                return minorCompare;
            }
        } else {
            return majorCompare;
        }
    }

    private int preReleaseCompare(@NotNull final Semver other) {
        if (!version.getPreRelease().isEmpty() && other.getPreRelease().isEmpty()) {
            return -1;
        } else if (version.getPreRelease().isEmpty() && !other.getPreRelease().isEmpty()) {
            return 1;
        } else if (version.getPreRelease().isEmpty() && other.getPreRelease().isEmpty()) {
            return 0;
        }

        int maxElements = max(version.getPreRelease().size(), other.getPreRelease().size());

        int i = 0;
        do {
            String a = getString(i, version.getPreRelease());
            String b = getString(i, other.getPreRelease());

            i++;

            if (a.equals(UNDEFINED_MARKER) && b.equals(UNDEFINED_MARKER)) {
                return 0;
            } else if (b.equals(UNDEFINED_MARKER)) {
                return 1;
            } else if (a.equals(UNDEFINED_MARKER)) {
                return -1;
            } else if (a.equals(b)) {
                continue;
            }

            return compareIdentifiers(a, b);
        } while (maxElements > i);

        return 0;
    }

    private int compareIdentifiers(@NotNull final String a, @NotNull final String b) {
        // Only attempt to parse fully-numeric string sequences so that we can avoid
        // raising a costly exception
        if (a.matches(ALL_DIGITS) && b.matches(ALL_DIGITS)) {
            long aAsLong = Long.parseLong(a);
            long bAsLong = Long.parseLong(b);
            return Long.compare(aAsLong, bAsLong);
        }

        if (a.matches(CONTAINS_DIGITS) && b.matches(CONTAINS_DIGITS)) {
            String[] tokenArr1 = a.split(TRAILING_DIGITS_EXTRACT);
            String[] tokenArr2 = b.split(TRAILING_DIGITS_EXTRACT);
            if (tokenArr1[0].equals(tokenArr2[0])) {
                String[] leadingDigitsArrA = tokenArr1[1].split(LEADING_DIGITS_EXTRACT);
                String[] leadingDigitsArrB = tokenArr2[1].split(LEADING_DIGITS_EXTRACT);
                long digitA = Long.parseLong(leadingDigitsArrA[0]);
                long digitB = Long.parseLong(leadingDigitsArrB[0]);
                int digitComparison = Long.compare(digitA, digitB);
                if (digitComparison != 0) {
                    return digitComparison;
                } else if (leadingDigitsArrA.length != leadingDigitsArrB.length) {
                    return leadingDigitsArrA.length - leadingDigitsArrB.length;
                } else {
                    return compareIdentifiers(leadingDigitsArrA[1], leadingDigitsArrB[1]);
                }
            }
        }

        int i = a.compareTo(b);
        if (i > 0) {
            return 1;
        } else if (i < 0) {
            return -1;
        }
        return 0;
    }

    @NotNull
    private String getString(final int i, @NotNull final List<@NotNull String> list) {
        if (list.size() > i) {
            return list.get(i);
        } else {
            return UNDEFINED_MARKER;
        }
    }
}
