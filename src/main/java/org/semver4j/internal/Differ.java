package org.semver4j.internal;

import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

import static org.semver4j.Semver.VersionDiff.*;

public class Differ {
    private final Semver version;

    public Differ(Semver version) {
        this.version = version;
    }

    public VersionDiff diff(Semver other) {
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
            return SUFFIX;
        }
        if (!version.getBuild().equals(other.getBuild())) {
            return BUILD;
        }
        return NONE;
    }
}
