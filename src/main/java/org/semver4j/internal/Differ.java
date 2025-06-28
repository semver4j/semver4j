package org.semver4j.internal;

import static org.semver4j.Semver.VersionDiff.*;

import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

/** Utility class for comparing semantic versions and determining the difference level between two version objects. */
public class Differ {
    /** Private constructor to prevent instantiation of this utility class. */
    private Differ() {}

    /**
     * Determines the difference level between two semantic versions.
     *
     * <p>Compares two semantic versions and returns an enum value indicating the highest level of difference between
     * them, based on the following hierarchy: {@code MAJOR > MINOR > PATCH > PRE_RELEASE > BUILD > NONE}.
     *
     * @param version the first semantic version to compare
     * @param other the second semantic version to compare
     * @return the highest level of difference between the two versions ({@link VersionDiff#MAJOR},
     *     {@link VersionDiff#MINOR}, {@link VersionDiff#PATCH}, {@link VersionDiff#PRE_RELEASE},
     *     {@link VersionDiff#BUILD}, or {@link VersionDiff#NONE})
     */
    public static VersionDiff diff(Semver version, Semver other) {
        if (version.getMajor() != other.getMajor()) {
            return MAJOR;
        }
        if (version.getMinor() != other.getMinor()) {
            return MINOR;
        }
        if (version.getPatch() != other.getPatch()) {
            return PATCH;
        }
        if (!version.getPreRelease().equals(other.getPreRelease())) {
            return PRE_RELEASE;
        }
        if (!version.getBuild().equals(other.getBuild())) {
            return BUILD;
        }
        return NONE;
    }
}
