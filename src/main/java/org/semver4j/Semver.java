package org.semver4j;

import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;
import org.semver4j.internal.*;
import org.semver4j.internal.VersionParser.Version;
import org.semver4j.range.RangeExpression;
import org.semver4j.range.RangeList;
import org.semver4j.range.RangeListFactory;

/**
 * {@code Semver} is a tool that provides useful methods to manipulate versions that follow the "semantic versioning"
 * specification (see <a href="http://semver.org">semver.org</a>).
 *
 * <p>This implementation adheres to the SemVer 2.0.0 specification and provides a comprehensive API for version
 * parsing, comparison, manipulation, and validation.
 */
public class Semver implements Comparable<Semver> {
    /** A constant {@link Semver} version object representing {@code 0.0.0}. */
    public static final Semver ZERO = new Semver("0.0.0");

    private final int major;
    private final int minor;
    private final int patch;
    private final List<String> preRelease;
    private final List<String> build;

    private final String version;

    /**
     * Constructs a new {@code Semver} instance by parsing the provided version string.
     *
     * @param version the version string to parse
     * @throws NullPointerException if the version is {@code null}
     * @throws IllegalArgumentException if the version string cannot be parsed according to SemVer specification
     */
    public Semver(String version) {
        requireNonNull(version, "version must not be null");

        Version parsedVersion = VersionParser.parse(version.trim());

        major = parsedVersion.major();
        minor = parsedVersion.minor();
        patch = parsedVersion.patch();
        preRelease = parsedVersion.preRelease();
        build = parsedVersion.build();

        this.version = new Builder()
                .withMajor(major)
                .withMinor(minor)
                .withPatch(patch)
                .withPreReleases(preRelease)
                .withBuilds(build)
                .toVersion();
    }

    /**
     * Try to parse string as a semver.
     *
     * @param version version string to parse
     * @return {@link Semver} when successfully parsed, {@code null} otherwise
     */
    public static @Nullable Semver parse(@Nullable String version) {
        if (version == null) {
            return null;
        }
        try {
            return new Semver(version);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Coerce string into semver if possible.
     *
     * <p>This method attempts to transform non-standard version strings into valid SemVer format before parsing.
     *
     * @param version version string to coerce
     * @return {@link Semver} if coercion and parsing succeed, {@code null} otherwise
     */
    public static @Nullable Semver coerce(@Nullable String version) {
        if (version == null) {
            return null;
        }

        Semver semver = parse(version);
        if (semver != null) {
            return semver;
        }

        String coercedVersion = Coerce.coerce(version);
        return parse(coercedVersion);
    }

    /**
     * Checks if a given string version is valid, according to SemVer specification.
     *
     * @param version version string to check
     * @return {@code true} if it is a valid version, {@code false} otherwise
     */
    public static boolean isValid(@Nullable String version) {
        return parse(version) != null;
    }

    /**
     * Returns builder instance to create {@code Semver} object.
     *
     * @return a new {@link Builder} instance
     * @since 6.0.0
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Returns a programmatically created basic {@link Semver} builder with the specified version components.
     *
     * @param major major version component
     * @param minor minor version component
     * @param patch patch version component
     * @return a {@link Builder} initialized with the specified version components
     * @since 5.3.0
     */
    public static Builder of(int major, int minor, int patch) {
        return builder().withMajor(major).withMinor(minor).withPatch(patch);
    }

    /**
     * Returns a programmatically created basic {@link Semver} object with the specified version components.
     *
     * @param major major version component
     * @param minor minor version component
     * @param patch patch version component
     * @return a {@link Semver} instance with the specified version components
     * @since 6.0.0
     */
    public static Semver create(int major, int minor, int patch) {
        return of(major, minor, patch).build();
    }

    /**
     * Returns the complete version string.
     *
     * @return the full version string in SemVer format
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the {@code major} part of the version.
     *
     * <p>Example: for {@code 1.2.3} = {@code 1}
     *
     * @return the {@code major} part of the version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Returns the {@code minor} part of the version.
     *
     * <p>Example: for {@code 1.2.3} = {@code 2}
     *
     * @return the {@code minor} part of the version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Returns the {@code patch} part of the version.
     *
     * <p>Example: for {@code 1.2.3} = {@code 3}
     *
     * @return the {@code patch} part of the version
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Returns the {@code pre-release} identifiers of the version.
     *
     * <p>Example: for {@code 1.2.3-beta.4+sha98450956} = {@code ["beta", "4"]}
     *
     * @return the list of {@code pre-release} identifiers, empty if none exist
     */
    public List<String> getPreRelease() {
        return preRelease;
    }

    /**
     * Returns the {@code build} identifiers of the version.
     *
     * <p>Example: for {@code 1.2.3-beta.4+sha98450956} = {@code ["sha98450956"]}
     *
     * @return the list of {@code build} identifiers, empty if none exist
     */
    public List<String> getBuild() {
        return build;
    }

    /**
     * Determines if the current version is stable or not.
     *
     * <p>Stable versions have a {@code major} version number <a href="https://semver.org/#spec-item-4">strictly
     * positive</a> and no <a href="https://semver.org/#spec-item-9">pre-release identifiers</a>.
     *
     * @return {@code true} if the current version is stable, {@code false} otherwise
     */
    public boolean isStable() {
        return major > 0 && preRelease.isEmpty();
    }

    /**
     * Increments a {@code major} version component to the next closest version.
     *
     * <p>This resets {@code minor} and {@code patch} to 0 and removes {@code pre-release} and {@code build}
     * identifiers.
     *
     * @return new {@link Semver} with an incremented {@code major} version
     */
    public Semver nextMajor() {
        return Modifier.nextMajor(this);
    }

    /**
     * Increments a {@code major} version component by one.
     *
     * @return new {@link Semver} with an incremented {@code major} version
     */
    public Semver withIncMajor() {
        return withIncMajor(1);
    }

    /**
     * Increments {@code major} version component by the specified amount.
     *
     * @param number the amount by which to increment the {@code major} version
     * @return new {@link Semver} with an incremented {@code major} version
     */
    public Semver withIncMajor(int number) {
        return Modifier.withIncMajor(this, number);
    }

    /**
     * Increments a {@code minor} version component to the next closest version.
     *
     * <p>This resets a {@code patch} to 0 and removes {@code pre-release} and build identifiers.
     *
     * @return new {@link Semver} with an incremented {@code minor} version
     */
    public Semver nextMinor() {
        return Modifier.nextMinor(this);
    }

    /**
     * Increments {@code minor} version component by one.
     *
     * @return new {@link Semver} with an incremented {@code minor} version
     */
    public Semver withIncMinor() {
        return withIncMinor(1);
    }

    /**
     * Increments a {@code minor} version component by the specified amount.
     *
     * @param number the amount by which to increment the {@code minor} version
     * @return new {@link Semver} with an incremented {@code minor} version
     */
    public Semver withIncMinor(int number) {
        return Modifier.withIncMinor(this, number);
    }

    /**
     * Increments {@code patch} a version component to the next closest version.
     *
     * <p>This removes {@code pre-release} and build identifiers.
     *
     * @return new {@link Semver} with an incremented {@code patch} version
     */
    public Semver nextPatch() {
        return Modifier.nextPatch(this);
    }

    /**
     * Increments {@code patch} version component by one.
     *
     * @return new {@link Semver} with an incremented {@code patch} version
     */
    public Semver withIncPatch() {
        return withIncPatch(1);
    }

    /**
     * Increments {@code patch} version component by the specified amount.
     *
     * @param number the amount by which to increment the {@code patch} version
     * @return new {@link Semver} with an incremented {@code patch} version
     */
    public Semver withIncPatch(int number) {
        return Modifier.withIncPatch(this, number);
    }

    /**
     * Sets {@code pre-release} version identifier.
     *
     * @param preRelease the {@code pre-release} identifier to set
     * @return new {@link Semver} with the specified {@code pre-release} identifier
     */
    public Semver withPreRelease(String preRelease) {
        return Modifier.withPreRelease(this, preRelease);
    }

    /**
     * Sets {@code build} version identifier.
     *
     * @param build the {@code build} identifier to set
     * @return new {@link Semver} with the specified {@code build} identifier
     */
    public Semver withBuild(String build) {
        return Modifier.withBuild(this, build);
    }

    /**
     * Removes {@code pre-release} identifiers from the version.
     *
     * @return new {@link Semver} without {@code pre-release} identifiers
     */
    public Semver withClearedPreRelease() {
        return Modifier.withClearedPreRelease(this);
    }

    /**
     * Removes {@code build} identifiers from the version.
     *
     * @return new {@link Semver} without {@code build} identifiers
     */
    public Semver withClearedBuild() {
        return Modifier.withClearedBuild(this);
    }

    /**
     * Removes both {@code pre-release} and {@code build} identifiers from the version.
     *
     * @return new {@link Semver} without {@code pre-release} and {@code build} identifiers
     */
    public Semver withClearedPreReleaseAndBuild() {
        return Modifier.withClearedPreReleaseAndBuild(this);
    }

    /**
     * Compares this version with another version according to SemVer precedence rules.
     *
     * @param other the version to compare with
     * @return a negative integer, zero, or a positive integer as this version is less than, equal to, or greater than
     *     the specified version
     */
    @Override
    public int compareTo(Semver other) {
        return Comparator.compareTo(this, other);
    }

    /**
     * Checks whether the given version is API compatible with this version.
     *
     * <p>API compatibility means the versions differ only in {@code minor}, {@code patch}, {@code pre-release}, or
     * {@code build} components.
     *
     * @param version version string to check for compatibility
     * @return {@code true} if the versions are API compatible, {@code false} otherwise
     * @see #isApiCompatible(Semver)
     */
    public boolean isApiCompatible(String version) {
        return isApiCompatible(new Semver(version));
    }

    /**
     * Checks whether the given version is API compatible with this version.
     *
     * <p>API compatibility means the versions differ only in {@code minor}, {@code patch}, {@code pre-release}, or
     * {@code build} components.
     *
     * @param version version object to check for compatibility
     * @return {@code true} if the versions are API compatible, {@code false} otherwise
     * @see #isApiCompatible(String)
     */
    public boolean isApiCompatible(Semver version) {
        return diff(version).ordinal() < VersionDiff.MAJOR.ordinal();
    }

    /**
     * Checks if this version is greater than another version.
     *
     * @param version version string to compare with
     * @return {@code true} if this version is greater than the provided version, {@code false} otherwise
     * @see #isGreaterThan(Semver)
     */
    public boolean isGreaterThan(String version) {
        return isGreaterThan(new Semver(version));
    }

    /**
     * Checks if this version is greater than another version.
     *
     * @param version version object to compare with
     * @return {@code true} if this version is greater than the provided version, {@code false} otherwise
     * @see #isGreaterThan(String)
     */
    public boolean isGreaterThan(Semver version) {
        return compareTo(version) > 0;
    }

    /**
     * Checks if this version is greater than or equal to another version.
     *
     * @param version version string to compare with
     * @return {@code true} if this version is greater than or equal to the provided version, {@code false} otherwise
     * @see #isGreaterThanOrEqualTo(Semver)
     */
    public boolean isGreaterThanOrEqualTo(String version) {
        return isGreaterThanOrEqualTo(new Semver(version));
    }

    /**
     * Checks if this version is greater than or equal to another version.
     *
     * @param version version object to compare with
     * @return {@code true} if this version is greater than or equal to the provided version, {@code false} otherwise
     * @see #isGreaterThanOrEqualTo(String)
     */
    public boolean isGreaterThanOrEqualTo(Semver version) {
        return compareTo(version) >= 0;
    }

    /**
     * Checks if this version is lower than another version.
     *
     * @param version version string to compare with
     * @return {@code true} if this version is lower than the provided version, {@code false} otherwise
     * @see #isLowerThan(Semver)
     */
    public boolean isLowerThan(String version) {
        return isLowerThan(new Semver(version));
    }

    /**
     * Checks if this version is lower than another version.
     *
     * @param version version object to compare with
     * @return {@code true} if this version is lower than the provided version, {@code false} otherwise
     * @see #isLowerThan(String)
     */
    public boolean isLowerThan(Semver version) {
        return compareTo(version) < 0;
    }

    /**
     * Checks if this version is lower than or equal to another version.
     *
     * @param version version string to compare with
     * @return {@code true} if this version is lower than or equal to the provided version, {@code false} otherwise
     * @see #isLowerThanOrEqualTo(Semver)
     */
    public boolean isLowerThanOrEqualTo(String version) {
        return isLowerThanOrEqualTo(new Semver(version));
    }

    /**
     * Checks if this version is lower than or equal to another version.
     *
     * @param version version object to compare with
     * @return {@code true} if this version is lower than or equal to the provided version, {@code false} otherwise
     * @see #isLowerThanOrEqualTo(String)
     */
    public boolean isLowerThanOrEqualTo(Semver version) {
        return compareTo(version) <= 0;
    }

    /**
     * Checks if this version exactly equals another version (including {@code pre-release} and {@code build}
     * identifiers).
     *
     * @param version version string to compare with
     * @return {@code true} if this version exactly equals the provided version, {@code false} otherwise
     * @see #isEqualTo(Semver)
     */
    public boolean isEqualTo(String version) {
        return isEqualTo(new Semver(version));
    }

    /**
     * Checks if this version exactly equals another version (including {@code pre-release} and {@code build}
     * identifiers).
     *
     * @param version version object to compare with
     * @return {@code true} if this version exactly equals the provided version, {@code false} otherwise
     * @see #isEqualTo(String)
     */
    public boolean isEqualTo(Semver version) {
        return equals(version);
    }

    /**
     * Checks if this version equals another version ignoring {@code build} identifiers.
     *
     * <p>This comparison includes all version components except {@code build} metadata.
     *
     * @param version version string to compare with
     * @return {@code true} if this version equals the provided version (ignoring build), {@code false} otherwise
     * @see #isEquivalentTo(Semver)
     */
    public boolean isEquivalentTo(String version) {
        return isEquivalentTo(new Semver(version));
    }

    /**
     * Checks if this version equals another version ignoring {@code build} identifiers.
     *
     * <p>This comparison includes all version components except {@code build} metadata.
     *
     * @param version version object to compare with
     * @return {@code true} if this version equals the provided version (ignoring build), {@code false} otherwise
     * @see #isEquivalentTo(String)
     */
    public boolean isEquivalentTo(Semver version) {
        return compareTo(version) == 0;
    }

    /**
     * Returns the greatest difference between this version and another version.
     *
     * <p>For example, if this version is {@code 1.2.3} and compared version is {@code 1.3.0}, the greatest difference
     * is the {@code MINOR} component.
     *
     * @param version version string to compare with
     * @return the greatest difference as a {@link VersionDiff} enum value
     * @see #diff(Semver)
     */
    public VersionDiff diff(String version) {
        return diff(new Semver(version));
    }

    /**
     * Returns the greatest difference between this version and another version.
     *
     * <p>For example, if this version is {@code 1.2.3} and compared version is {@code 1.3.0}, the greatest difference
     * is the {@code MINOR} component.
     *
     * @param version version object to compare with
     * @return the greatest difference as a {@link VersionDiff} enum value
     * @see #diff(String)
     */
    public VersionDiff diff(Semver version) {
        return Differ.diff(this, version);
    }

    /**
     * Checks if this version satisfies the specified version range.
     *
     * <p>By default, {@code pre-release} versions are not included in the check.
     *
     * @param range version range expression to check against
     * @return {@code true} if this version satisfies the range, {@code false} otherwise
     * @see #satisfies(RangeList)
     * @see #satisfies(RangeExpression)
     */
    public boolean satisfies(String range) {
        return satisfies(range, false);
    }

    /**
     * Checks if this version satisfies the specified version range.
     *
     * <p>Allows control over whether {@code pre-release} versions are included in the check.
     *
     * @param range version range expression to check against
     * @param includePreRelease whether to include {@code pre-releases} in the check
     * @return {@code true} if this version satisfies the range, {@code false} otherwise
     * @see #satisfies(String)
     * @see #satisfies(RangeList)
     * @since 5.8.0
     */
    public boolean satisfies(String range, boolean includePreRelease) {
        RangeList rangeList = RangeListFactory.create(range, includePreRelease);
        return satisfies(rangeList);
    }

    /**
     * Checks if this version satisfies the specified ranges expression.
     *
     * @param rangeExpression ranges expression built via internal expressions mechanism
     * @return {@code true} if this version satisfies the range, {@code false} otherwise
     * @see RangeExpression
     * @see #satisfies(String)
     * @see #satisfies(RangeList)
     * @since 4.2.0
     */
    public boolean satisfies(RangeExpression rangeExpression) {
        RangeList rangeList = RangeListFactory.create(rangeExpression);
        return satisfies(rangeList);
    }

    /**
     * Checks if this version satisfies the specified ranges list.
     *
     * @param rangeList list of version ranges to check against
     * @return {@code true} if this version satisfies the ranges list, {@code false} otherwise
     * @see #satisfies(String)
     * @see #satisfies(RangeExpression)
     */
    public boolean satisfies(RangeList rangeList) {
        return rangeList.isSatisfiedBy(this);
    }

    /**
     * Formats this version using a custom formatter function.
     *
     * @param formatter function that converts a {@link Semver} to a formatted string
     * @return the formatted version string
     */
    public String format(Function<Semver, String> formatter) {
        return formatter.apply(this);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Semver)) {
            return false;
        }
        return version.equals(((Semver) o).version);
    }

    @Override
    public int hashCode() {
        return hash(version);
    }

    @Override
    public String toString() {
        return getVersion();
    }

    /**
     * The types of differences between the two versions.
     *
     * <p>The higher the ordinal value of the enum, the greater the difference between versions.
     */
    public enum VersionDiff {
        /** No difference between versions */
        NONE,
        /** Versions differ only in {@code build} metadata */
        BUILD,
        /** Versions differ in {@code pre-release} identifiers */
        PRE_RELEASE,
        /** Versions differ in {@code patch} version */
        PATCH,
        /** Versions differ in {@code minor} version */
        MINOR,
        /** Versions differ in {@code major} version */
        MAJOR
    }

    /**
     * A builder for creating {@link Semver} instances.
     *
     * <p>This builder allows constructing versions programmatically.
     *
     * @since 5.3.0
     */
    public static class Builder {
        private int major;
        private int minor;
        private int patch;
        private List<String> preRelease = emptyList();
        private List<String> build = emptyList();

        /**
         * Sets the {@code major} version component.
         *
         * @param major the major version number
         * @return this builder instance
         */
        public Builder withMajor(int major) {
            this.major = major;
            return this;
        }

        /**
         * Sets the {@code minor} version component.
         *
         * @param minor the minor version number
         * @return this builder instance
         */
        public Builder withMinor(int minor) {
            this.minor = minor;
            return this;
        }

        /**
         * Sets the {@code patch} version component.
         *
         * @param patch the patch version number
         * @return this builder instance
         */
        public Builder withPatch(int patch) {
            this.patch = patch;
            return this;
        }

        /**
         * Sets a single {@code pre-release} identifier.
         *
         * @param preRelease the {@code pre-release} identifier
         * @return this builder instance
         * @throws NullPointerException if preRelease is {@code null}
         */
        public Builder withPreRelease(String preRelease) {
            requireNonNull(preRelease, "preRelease cannot be null");
            return withPreReleases(List.of(preRelease));
        }

        /**
         * Sets multiple {@code pre-release} identifiers from a collection.
         *
         * @param preReleases collection of {@code pre-release} identifiers
         * @return this builder instance
         * @throws NullPointerException if preReleases is {@code null}
         */
        public Builder withPreReleases(Collection<String> preReleases) {
            requireNonNull(preReleases, "preReleases cannot be null");
            this.preRelease = List.copyOf(preReleases);
            return this;
        }

        /**
         * Sets multiple {@code pre-release} identifiers from varargs.
         *
         * @param preReleases array of {@code pre-release} identifiers
         * @return this builder instance
         * @throws NullPointerException if preReleases is {@code null}
         */
        public Builder withPreReleases(String... preReleases) {
            requireNonNull(preReleases, "preReleases cannot be null");
            this.preRelease = List.of(preReleases);
            return this;
        }

        /**
         * Sets a single {@code build} identifier.
         *
         * @param build the {@code build} identifier
         * @return this builder instance
         * @throws NullPointerException if build is {@code null}
         */
        public Builder withBuild(String build) {
            requireNonNull(build, "build cannot be null");
            return withBuilds(List.of(build));
        }

        /**
         * Sets multiple {@code build} identifiers from a collection.
         *
         * @param builds collection of {@code build} identifiers
         * @return this builder instance
         * @throws NullPointerException if builds is {@code null}
         */
        public Builder withBuilds(Collection<String> builds) {
            requireNonNull(builds, "builds cannot be null");
            this.build = List.copyOf(builds);
            return this;
        }

        /**
         * Sets multiple {@code build} identifiers from varargs.
         *
         * @param builds array of {@code build} identifiers
         * @return this builder instance
         * @throws NullPointerException if builds is {@code null}
         */
        public Builder withBuilds(String... builds) {
            requireNonNull(builds, "builds cannot be null");
            this.build = List.of(builds);
            return this;
        }

        /**
         * Builds a new {@link Semver} instance from this builder's configuration.
         *
         * @return a new {@link Semver} instance
         */
        public Semver build() {
            String version = toVersion();
            return new Semver(version);
        }

        /**
         * Builds a string representation of the version.
         *
         * <p>Follows the SemVer specification format: {@code MAJOR.MINOR.PATCH[-PRERELEASE][+BUILD]}
         *
         * @return the formatted version string
         */
        private String toVersion() {
            String resultVersion = String.format(Locale.ROOT, "%d.%d.%d", major, minor, patch);
            if (!preRelease.isEmpty()) {
                resultVersion += "-" + join(".", preRelease);
            }
            if (!build.isEmpty()) {
                resultVersion += "+" + join(".", build);
            }
            return resultVersion;
        }
    }
}
