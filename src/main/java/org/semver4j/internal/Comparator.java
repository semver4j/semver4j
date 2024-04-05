package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;

import java.util.List;

import static java.lang.Math.max;

public class Comparator implements Comparable<Semver> {
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
        int majorCompare = compareIdentifiers(version.getMajor(), other.getMajor());
        if (majorCompare == 0) {
            int minorCompare = compareIdentifiers(version.getMinor(), other.getMinor());
            if (minorCompare == 0) {
                return compareIdentifiers(version.getPatch(), other.getPatch());
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
        try {
            long aAsLong = Long.parseLong(a);
            long bAsLong = Long.parseLong(b);
            return compareIdentifiers(aAsLong, bAsLong);
        } catch (NumberFormatException e) {
            //ignore
        }

        if (isBothContainsDigits(a, b)) {
            String digitsExtract = "(?<=\\D)(?=\\d)";
            String[] tokenArr1 = a.split(digitsExtract);
            String[] tokenArr2 = b.split(digitsExtract);
            if (tokenArr1[0].equals(tokenArr2[0])) {
                long digitA = Long.parseLong(tokenArr1[1]);
                long digitB = Long.parseLong(tokenArr2[1]);
                return compareIdentifiers(digitA, digitB);
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

    private int compareIdentifiers(long a, long b) {
        return Long.compare(a, b);
    }

    private boolean isBothContainsDigits(@NotNull final String a, @NotNull final String b) {
        return a.matches(".*\\d.*") && b.matches(".*\\d.*");
    }

    @NotNull
    private String getString(final int i, @NotNull final List<@NotNull String> list) {
        try {
            return list.get(i);
        } catch (IndexOutOfBoundsException e) {
            return UNDEFINED_MARKER;
        }
    }
}
