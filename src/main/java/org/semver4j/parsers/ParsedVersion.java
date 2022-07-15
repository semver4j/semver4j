package org.semver4j.parsers;

import java.util.Objects;
import java.util.StringJoiner;

public class ParsedVersion {
    private final int major;
    private final Integer minor;
    private final Integer patch;
    private final String preRelease;
    private final String build;

    ParsedVersion(int major, Integer minor, Integer patch, String preRelease, String build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.preRelease = preRelease;
        this.build = build;
    }

    ParsedVersion(int major, int minor, int patch) {
        this(major, minor, patch, null, null);
    }

    public int getMajor() {
        return major;
    }

    public Integer getMinor() {
        return minor;
    }

    public Integer getPatch() {
        return patch;
    }

    public String getPreRelease() {
        return preRelease;
    }

    public String getBuild() {
        return build;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ParsedVersion that = (ParsedVersion) o;
        return major == that.major &&
                Objects.equals(minor, that.minor) &&
                Objects.equals(patch, that.patch) &&
                Objects.equals(preRelease, that.preRelease) &&
                Objects.equals(build, that.build);
    }

    @Override
    public int hashCode() {
        return Objects.hash(major, minor, patch, preRelease, build);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParsedVersion.class.getSimpleName() + "[", "]")
                .add("major=" + major)
                .add("minor=" + minor)
                .add("patch=" + patch)
                .add("preRelease='" + preRelease + "'")
                .add("build='" + build + "'")
                .toString();
    }
}
