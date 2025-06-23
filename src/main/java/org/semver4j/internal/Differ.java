package org.semver4j.internal;

import static org.semver4j.Semver.VersionDiff.*;

import org.jspecify.annotations.NullMarked;
import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

@NullMarked
public class Differ {
    private Differ() {}

    public static VersionDiff diff(final Semver version, final Semver other) {
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
