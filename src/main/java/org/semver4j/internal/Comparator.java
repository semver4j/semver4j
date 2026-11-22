package org.semver4j.internal;

import java.math.BigInteger;
import java.util.List;
import org.semver4j.Semver;

/**
 * Utility class for comparing semantic versions according to the SemVer specification. This class provides
 * functionality to compare two {@link Semver} objects.
 */
public class Comparator {
    private static final String ALL_DIGITS = "^\\d+$";

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

        List<String> identifiers = version.getPreRelease();
        List<String> otherIdentifiers = other.getPreRelease();
        for (int i = 0; i < Math.min(identifiers.size(), otherIdentifiers.size()); i++) {
            int result = compareIdentifiers(identifiers.get(i), otherIdentifiers.get(i));
            if (result != 0) {
                return result;
            }
        }
        return Integer.compare(identifiers.size(), otherIdentifiers.size());
    }

    private static int compareIdentifiers(String a, String b) {
        // Only attempt to parse fully numeric string sequences so that we can avoid
        // raising a costly exception. BigInteger is used because the SemVer spec
        // places no upper bound on numeric identifiers, so they can exceed a long.
        boolean aIsNumeric = a.matches(ALL_DIGITS);
        boolean bIsNumeric = b.matches(ALL_DIGITS);
        if (aIsNumeric && bIsNumeric) {
            return new BigInteger(a).compareTo(new BigInteger(b));
        }
        if (aIsNumeric != bIsNumeric) {
            return aIsNumeric ? -1 : 1;
        }
        return Integer.signum(a.compareTo(b));
    }
}
