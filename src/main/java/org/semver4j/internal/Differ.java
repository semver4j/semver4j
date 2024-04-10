package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

import static org.semver4j.Semver.VersionDiff.*;

public class Differ {
    private Differ() {
    }

    @NotNull
    public static VersionDiff diff(@NotNull final Semver version, @NotNull final Semver other) {
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
