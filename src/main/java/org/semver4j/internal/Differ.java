package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

import static org.semver4j.Semver.VersionDiff.*;

public class Differ {
    @NotNull
    private final Semver version;

    public Differ(@NotNull final Semver version) {
        this.version = version;
    }

    @NotNull
    public VersionDiff diff(@NotNull final Semver other) {
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
