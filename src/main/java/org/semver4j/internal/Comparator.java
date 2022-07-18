package org.semver4j.internal;

import org.semver4j.Semver;

import java.util.List;

import static java.lang.Math.max;

public class Comparator implements Comparable<Semver> {
    private static final String UNDEFINED_MARKER = "undef";

    private final Semver version;

    public Comparator(Semver version) {
        this.version = version;
    }

    @Override
    public int compareTo(Semver other) {
        int result = mainCompare(other);
        if (result == 0) {
            return preReleaseCompare(other);
        }
        return result;
    }

    private int mainCompare(Semver other) {
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

    private int preReleaseCompare(Semver other) {
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

    private int compareIdentifiers(String a, String b) {
        try {
            int aAsInt = Integer.parseInt(a);
            int bAsInt = Integer.parseInt(b);
            return compareIdentifiers(aAsInt, bAsInt);
        } catch (NumberFormatException e) {
            //ignore
        }

        int i = a.compareTo(b);
        if (i > 0) {
            return 1;
        } else if (i < 0) {
            return -1;
        }
        return 0;
    }

    private int compareIdentifiers(int a, int b) {
        return Integer.compare(a, b);
    }

    private String getString(int i, List<String> list) {
        try {
            return list.get(i);
        } catch (IndexOutOfBoundsException e) {
            return UNDEFINED_MARKER;
        }
    }
}
