package org.semver4j;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.semver4j.internal.*;
import org.semver4j.internal.Comparator;
import org.semver4j.internal.StrictParser.Version;

import java.util.*;
import java.util.function.Function;

import static java.lang.String.join;
import static java.util.Collections.emptyList;
import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

/**
 * Semver is a tool that provides useful methods to manipulate versions that follow the "semantic versioning"
 * specification (see <a href="http://semver.org">semver.org</a>).
 */
@NullMarked
public class Semver implements Comparable<Semver> {
    public static final Semver ZERO = new Semver("0.0.0");

    public static final String LOWEST_PRERELEASE = "-0";

    private final String originalVersion;

    private final int major;
    private final int minor;
    private final int patch;
    private final List<String> preRelease;
    private final List<String> build;

    private final String version;

    public Semver(final String version) {
        this.originalVersion = version.trim();

        Version parsedVersion = StrictParser.parse(this.originalVersion);

        major = parsedVersion.getMajor();
        minor = parsedVersion.getMinor();
        patch = parsedVersion.getPatch();
        preRelease = parsedVersion.getPreRelease();
        build = parsedVersion.getBuild();

        this.version = new Builder()
                .withMajor(major)
                .withMinor(minor)
                .withPatch(patch)
                .withPreReleases(preRelease)
                .withBuilds(build)
                .toVersion();
    }

    /**
     * Try to parse string as semver.
     *
     * @param version version string to parse
     * @return {@link Semver} when done, {@code null} otherwise
     */
    @Nullable
    public static Semver parse(final @Nullable String version) {
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
     * Coerce string into semver if is possible.
     *
     * @param version version to coerce
     * @return {@link Semver} if can coerce version, {@code null} otherwise
     */
    @Nullable
    public static Semver coerce(final @Nullable String version) {
        if (version == null) {
            return null;
        }

        Semver semver = parse(version);
        if (semver != null) {
            return semver;
        }

        String coerce = Coerce.coerce(version);
        return parse(coerce);
    }

    /**
     * Checks is given string version is valid.
     *
     * @param version version to check
     * @return {@code true} if is valid version, {@code false} otherwise
     */
    public static boolean isValid(final @Nullable String version) {
        return parse(version) != null;
    }

    /**
     * Returns builder instance to create semver.
     *
     * @since 5.3.0
     */
    public static Builder of() {
        return new Builder();
    }

    /**
     * Returns a programmatically created basic {@link Semver} object.
     *
     * @param major version to set
     * @param minor version to set
     * @param patch version to set
     * @return basic {@link Semver} object
     * @since 5.3.0
     */
    public static Semver of(int major, int minor, int patch) {
        return new Builder()
                .withMajor(major)
                .withMinor(minor)
                .withPatch(patch)
                .toSemver();
    }

    /**
     * Returns the version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the major part of the version.
     * Example: for "1.2.3" = 1
     *
     * @return the major part of the version
     */
    public int getMajor() {
        return major;
    }

    /**
     * Returns the minor part of the version.
     * Example: for "1.2.3" = 2
     *
     * @return the minor part of the version
     */
    public int getMinor() {
        return minor;
    }

    /**
     * Returns the patch part of the version.
     * Example: for "1.2.3" = 3
     *
     * @return the patch part of the version
     */
    public int getPatch() {
        return patch;
    }

    /**
     * Returns the pre-release of the version.
     * Example: for "1.2.3-beta.4+sha98450956" = {"beta", "4"}
     *
     * @return the pre-release of the version
     */
    public List<String> getPreRelease() {
        return preRelease;
    }

    /**
     * Returns the build of the version.
     * Example: for "1.2.3-beta.4+sha98450956" = "sha98450956"
     *
     * @return the build of the version
     */
    public List<String> getBuild() {
        return build;
    }

    /**
     * Determines if the current version is stable or not.
     * Stable version have a major version number <a href="https://semver.org/#spec-item-4">strictly positive</a>
     * and no <a href="https://semver.org/#spec-item-9">pre-release tokens</a>.
     *
     * @return true if the current version is stable
     */
    public boolean isStable() {
        return major > 0 && preRelease.isEmpty();
    }

    /**
     * Increments major to the next closest version.
     *
     * @return new incremented semver
     */
    public Semver nextMajor() {
        return Modifier.nextMajor(this);
    }

    /**
     * Increments major with one.
     *
     * @return new incremented semver
     */
    public Semver withIncMajor() {
        return withIncMajor(1);
    }

    /**
     * Increments major with defined number.
     *
     * @param number how many major should be incremented
     * @return new incremented semver
     */
    public Semver withIncMajor(int number) {
        return Modifier.withIncMajor(this, number);
    }

    /**
     * Increments minor to the next closest version.
     *
     * @return new incremented semver
     */
    public Semver nextMinor() {
        return Modifier.nextMinor(this);
    }

    /**
     * Increments minor with one.
     *
     * @return new incremented semver
     */
    public Semver withIncMinor() {
        return withIncMinor(1);
    }

    /**
     * Increments minor with defined number.
     *
     * @param number how many minor should be incremented
     * @return new incremented semver
     */
    public Semver withIncMinor(int number) {
        return Modifier.withIncMinor(this, number);
    }

    /**
     * Increments patch to the next closest version.
     *
     * @return new incremented semver
     */
    public Semver nextPatch() {
        return Modifier.nextPatch(this);
    }

    /**
     * Increments patch with one.
     *
     * @return new incremented semver
     */
    public Semver withIncPatch() {
        return withIncPatch(1);
    }

    /**
     * Increments patch with defined number.
     *
     * @param number how many patch should be incremented
     * @return new incremented semver
     */
    public Semver withIncPatch(int number) {
        return Modifier.withIncPatch(this, number);
    }

    /**
     * Sets pre-release version.
     *
     * @param preRelease version to set
     * @return semver with new pre-release
     */
    public Semver withPreRelease(final String preRelease) {
        return Modifier.withPreRelease(this, preRelease);
    }

    /**
     * Sets build version.
     *
     * @param build version to set
     * @return semver with new build
     */
    public Semver withBuild(final String build) {
        return Modifier.withBuild(this, build);
    }

    /**
     * Removes pre-release from semver.
     *
     * @return semver without pre-release
     */
    public Semver withClearedPreRelease() {
        return Modifier.withClearedPreRelease(this);
    }

    /**
     * Removes build from semver.
     *
     * @return semver without build
     */
    public Semver withClearedBuild() {
        return Modifier.withClearedBuild(this);
    }

    /**
     * Removes both pre-release and build from semver.
     *
     * @return semver without pre-release and build
     */
    public Semver withClearedPreReleaseAndBuild() {
        return Modifier.withClearedPreReleaseAndBuild(this);
    }

    @Override
    public int compareTo(final Semver other) {
        return Comparator.compareTo(this, other);
    }

    /**
     * Checks whether the given version is API compatible with this version.
     */
    public boolean isApiCompatible(final String version) {
        return diff(version).ordinal() < VersionDiff.MAJOR.ordinal();
    }

    /**
     * Checks whether the given version is API compatible with this version.
     */
    @SuppressWarnings("unused")
    public boolean isApiCompatible(final Semver version) {
        return diff(version).ordinal() < VersionDiff.MAJOR.ordinal();
    }

    /**
     * Checks if the version is greater than another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is greater than the provided version, {@code false} otherwise
     * @see #isGreaterThan(Semver)
     */
    public boolean isGreaterThan(final String version) {
        return isGreaterThan(new Semver(version));
    }

    /**
     * Checks if the version is greater than another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is greater than the provided version, {@code false} otherwise
     * @see #isGreaterThan(String)
     */
    public boolean isGreaterThan(final Semver version) {
        return compareTo(version) > 0;
    }

    /**
     * Checks if the version is greater than or equal to another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is greater than or equal to the provided version, {@code false} otherwise
     * @see #isGreaterThanOrEqualTo(Semver)
     */
    public boolean isGreaterThanOrEqualTo(final String version) {
        return isGreaterThanOrEqualTo(new Semver(version));
    }

    /**
     * Checks if the version is greater than or equal to another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is greater than or equal to the provided version, {@code false} otherwise
     * @see #isLowerThan(String)
     */
    public boolean isGreaterThanOrEqualTo(final Semver version) {
        return compareTo(version) >= 0;
    }

    /**
     * Checks if the version is lower than another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is lower than the provided version, {@code false} otherwise
     * @see #isLowerThan(Semver)
     */
    public boolean isLowerThan(final String version) {
        return isLowerThan(new Semver(version));
    }

    /**
     * Checks if the version is lower than another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is lower than the provided version, {@code false} otherwise
     * @see #isLowerThan(String)
     */
    public boolean isLowerThan(final Semver version) {
        return compareTo(version) < 0;
    }

    /**
     * Checks if the version is lower than or equal to another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is lower than or equal to the provided version, {@code false} otherwise
     * @see #isLowerThanOrEqualTo(Semver)
     */
    public boolean isLowerThanOrEqualTo(final String version) {
        return isLowerThanOrEqualTo(new Semver(version));
    }

    /**
     * Checks if the version is lower than or equal to another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version is lower than or equal to the provided version, {@code false} otherwise
     * @see #isLowerThanOrEqualTo(String)
     */
    public boolean isLowerThanOrEqualTo(final Semver version) {
        return compareTo(version) <= 0;
    }

    /**
     * Checks if the version equals (exact compare) another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version, {@code false} otherwise
     * @see #isEqualTo(Semver)
     */
    public boolean isEqualTo(final String version) {
        return isEqualTo(new Semver(version));
    }

    /**
     * Checks if the version equals (exact compare) another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version, {@code false} otherwise
     * @see #isEqualTo(String)
     */
    public boolean isEqualTo(final Semver version) {
        return equals(version);
    }

    /**
     * Checks if the version equals another version, without taking the build into account.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version (build excluded), {@code false} otherwise
     * @see #isEquivalentTo(Semver)
     */
    public boolean isEquivalentTo(final String version) {
        return isEquivalentTo(new Semver(version));
    }

    /**
     * Checks if the version equals another version, without taking the build into account.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version (build excluded), {@code false} otherwise
     * @see #isEquivalentTo(String)
     */
    public boolean isEquivalentTo(final Semver version) {
        return compareTo(version) == 0;
    }

    /**
     * Returns the <b>greatest</b> difference between two versions.<br>
     * example, if the current version is {@code 1.2.3} and compared version is {@code 1.3.0}, the biggest difference
     * is the <b>MINOR</b> number.
     *
     * @param version version to compare
     * @return the greatest difference
     * @see #diff(Semver)
     */
    public VersionDiff diff(final String version) {
        return diff(new Semver(version));
    }

    /**
     * Returns the <b>greatest</b> difference between two versions.<br>
     * For example, if the current version is {@code 1.2.3} and compared version is {@code 1.3.0}, the biggest difference
     * is the <b>MINOR</b> number.
     *
     * @param version version to compare
     * @return the greatest difference
     * @see #diff(String)
     */
    public VersionDiff diff(final Semver version) {
        return Differ.diff(this, version);
    }

    /**
     * Check if the version satisfies a range. Defaults to not including prereleases.
     *
     * @param range range
     * @return {@code true} if the version satisfies the range, {@code false} otherwise
     * @see #satisfies(RangesList)
     * @see #satisfies(RangesExpression)
     */
    public boolean satisfies(final String range) {
        return satisfies(range, false);
    }

    public boolean satisfies(final String range, boolean includePrerelease) {
        RangesList rangesList = RangesListFactory.create(range, includePrerelease);
        if (includePrerelease) {
            rangesList.includePrerelease();
        }
        return satisfies(rangesList);
    }

    /**
     * Check if the version build by expressions satisfies a range.
     *
     * @param rangesExpression build via internal expressions mechanism
     * @return {@code true} if the version satisfies the range, {@code false} otherwise
     * @see RangesExpression
     * @see #satisfies(String)
     * @see #satisfies(RangesList)
     * @since 4.2.0
     */
    public boolean satisfies(final RangesExpression rangesExpression) {
        RangesList rangesList = RangesListFactory.create(rangesExpression);
        return satisfies(rangesList);
    }

    /**
     * Check if the version satisfies a ranges list.
     *
     * @param rangesList list with the ranges
     * @return {@code true} if the version satisfies the ranges list, {@code false} otherwise
     * @see #satisfies(String)
     * @see #satisfies(RangesExpression)
     */
    public boolean satisfies(final RangesList rangesList) {
        return rangesList.isSatisfiedBy(this);
    }

    /**
     * Format {@link Semver} object using custom formatting rules.
     */
    public String format(Function<Semver, String> formatter) {
        return formatter.apply(this);
    }

    @Override
    public boolean equals(final @Nullable Object o) {
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
     * The types of diffs between two versions. The higher the ordinal value of the enum is, the greater is the diff.
     */
    public enum VersionDiff {
        NONE,
        BUILD,
        PRE_RELEASE,
        PATCH,
        MINOR,
        MAJOR
    }

    /**
     * A builder for creating a {@link Semver}.<br>
     * It also provides a string representation of the version through {@link Builder#toVersion()}.
     *
     * @since 5.3.0
     */
    public static class Builder {
        private int major;
        private int minor;
        private int patch;
        private List<String> preRelease = emptyList();
        private List<String> build = emptyList();

        public Builder withMajor(int major) {
            this.major = major;
            return this;
        }

        public Builder withMinor(int minor) {
            this.minor = minor;
            return this;
        }

        public Builder withPatch(int patch) {
            this.patch = patch;
            return this;
        }

        @SuppressWarnings("unused")
        public Builder withPreRelease(String preRelease) {
            requireNonNull(preRelease, "preRelease cannot be null");
            return withPreReleases(new String[]{preRelease});
        }

        public Builder withPreReleases(Collection<String> preReleases) {
            requireNonNull(preReleases, "preRelease cannot be null");
            this.preRelease = new ArrayList<>(preReleases);
            return this;
        }

        public Builder withPreReleases(String[] preReleases) {
            requireNonNull(preReleases, "preRelease cannot be null");
            this.preRelease = Arrays.asList(preReleases);
            return this;
        }

        @SuppressWarnings("unused")
        public Builder withBuild(String build) {
            requireNonNull(build, "build cannot be null");
            return withBuilds(new String[]{build});
        }

        public Builder withBuilds(Collection<String> builds) {
            requireNonNull(builds, "builds cannot be null");
            this.build = new ArrayList<>(builds);
            return this;
        }

        public Builder withBuilds(String[] builds) {
            requireNonNull(builds, "builds cannot be null");
            this.build = Arrays.asList(builds);
            return this;
        }

        /**
         * Build a {@link Semver} object.
         */
        public Semver toSemver() {
            String version = toVersion();
            return new Semver(version);
        }

        /**
         * Build a string representation of the version.<p>
         * It follows a semver specification which results in:
         * {@code 1.2.3-alpha+5bb76cdb}
         */
        public String toVersion() {
            String resultVersion = String.format(Locale.ROOT, "%d.%d.%d", major, minor, patch);
            if (!preRelease.isEmpty()) {
                resultVersion += "-" + join(".", preRelease);
            }
            if (!build.isEmpty()) {
                resultVersion += "+" + join(".", build);
            }
            return resultVersion;
        }

        /**
         * Format {@link Semver} object using custom formatting rules.
         */
        public String toVersion(Function<Semver, String> formatter) {
            return formatter.apply(toSemver());
        }
    }
}
