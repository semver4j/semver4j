package org.semver4j.internal;

import static java.lang.Math.max;

import java.util.List;
import org.jspecify.annotations.Nullable;
import org.semver4j.Semver;

/**
 * Utility class for comparing semantic versions according to the SemVer specification. This class provides
 * functionality to compare two {@link Semver} objects.
 */
public class Comparator {
    private static final String ALL_DIGITS = "^\\d+$";
    private static final String CONTAINS_DIGITS = ".*\\d.*";
    private static final String TRAILING_DIGITS_EXTRACT = "(?<=\\D)(?=\\d)";
    private static final String LEADING_DIGITS_EXTRACT = "(?<=\\d)(?=\\D)";

    private static final String UNDEFINED_MARKER = "undef";

    /** Private constructor to prevent instantiation of this utility class. */
    private Comparator() {}

    /**
     * Compares two semantic versions.
     *
     * @param version the first version to compare
     * @param other the second version to compare
     * @return a negative integer if version is less than other, zero if they are equal, a positive integer if version
     *     is greater than other
     */
    public static int compareTo(Semver version, Semver other) {
        int result = mainCompare(version, other);
        if (result == 0) {
            return preReleaseCompare(version, other);
        }
        return result;
    }

    private static int mainCompare(Semver version, Semver other) {
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

    private static int preReleaseCompare(Semver version, Semver other) {
        if (!version.getPreRelease().isEmpty() && other.getPreRelease().isEmpty()) {
            return -1;
        } else if (version.getPreRelease().isEmpty() && !other.getPreRelease().isEmpty()) {
            return 1;
        } else if (version.getPreRelease().isEmpty() && other.getPreRelease().isEmpty()) {
            return 0;
        }

        int maxElements =
                max(version.getPreRelease().size(), other.getPreRelease().size());

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

    private static int compareIdentifiers(String a, String b) {
        // Only attempt to parse fully numeric string sequences so that we can avoid
        // raising a costly exception
        if (a.matches(ALL_DIGITS) && b.matches(ALL_DIGITS)) {
            long aAsLong = Long.parseLong(a);
            long bAsLong = Long.parseLong(b);
            return Long.compare(aAsLong, bAsLong);
        }

        if (a.matches(CONTAINS_DIGITS) && b.matches(CONTAINS_DIGITS)) {
            Integer alphaNumericComparison = checkAlphanumericPrerelease(a, b);
            if (alphaNumericComparison != null) {
                return alphaNumericComparison;
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

    private static @Nullable Integer checkAlphanumericPrerelease(String a, String b) {
        String[] tokenArrA = a.split(TRAILING_DIGITS_EXTRACT);
        String[] tokenArrB = b.split(TRAILING_DIGITS_EXTRACT);
        if (tokenArrA.length != tokenArrB.length) {
            return tokenArrA.length - tokenArrB.length;
        }
        if (tokenArrA[0].equals(tokenArrB[0])) {
            String[] leadingDigitsArrA = tokenArrA[1].split(LEADING_DIGITS_EXTRACT);
            String[] leadingDigitsArrB = tokenArrB[1].split(LEADING_DIGITS_EXTRACT);
            long digitA = Long.parseLong(leadingDigitsArrA[0]);
            long digitB = Long.parseLong(leadingDigitsArrB[0]);
            int digitComparison = Long.compare(digitA, digitB);
            if (digitComparison != 0) {
                return digitComparison;
            } else if (leadingDigitsArrA.length != leadingDigitsArrB.length) {
                return leadingDigitsArrA.length - leadingDigitsArrB.length;
            } else {
                return compareIdentifiers(
                        a.substring(a.indexOf(leadingDigitsArrA[0]) + 1),
                        b.substring(b.indexOf(leadingDigitsArrB[0]) + 1));
            }
        }
        return null;
    }

    private static String getString(int i, List<String> list) {
        return list.size() > i ? list.get(i) : UNDEFINED_MARKER;
    }
}
