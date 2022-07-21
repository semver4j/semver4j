package org.semver4j;

import org.semver4j.internal.*;
import org.semver4j.internal.StrictParser.Version;
import org.semver4j.internal.range.RangesListFactory;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.hash;
import static org.semver4j.Semver.SemverType.STRICT;

/**
 * Semver is a tool that provides useful methods to manipulate versions that follow the "semantic versioning"
 * specification (see <a href="http://semver.org">semver.org</a>).
 */
public class Semver implements Comparable<Semver> {
    private static final StrictParser STRICT_PARSER = new StrictParser();

    private final String version;
    private final SemverType type;

    private final int major;
    private final int minor;
    private final int patch;
    private final List<String> preRelease;
    private final List<String> build;

    public Semver(String version) {
        this(version, STRICT);
    }

    public Semver(String version, SemverType type) {
        this.version = version.trim();
        this.type = type;

        Version parsedVersion = STRICT_PARSER.parse(this.version);

        major = parsedVersion.getMajor();
        minor = parsedVersion.getMinor();
        patch = parsedVersion.getPatch();
        preRelease = parsedVersion.getPreRelease();
        build = parsedVersion.getBuild();
    }

    /**
     * Try to parse string as semver.
     *
     * @param version version string to parse
     * @return {@link Semver} when done, {@code null} otherwise
     */
    public static Semver parse(String version) {
        try {
            return new Semver(version);
        } catch (Exception e) {
            return null;
        }
    }

    public static Semver coerce(String version) {
        String coerce = Coerce.coerce(version);
        return parse(coerce);
    }

    /**
     * Returns the version.
     *
     * @return version
     */
    public String getVersion() {
        return version;
    }

    public SemverType getType() {
        return type;
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
     * @return the suffix of the version
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
     * and no <a href="https://semver.org/#spec-item-9">prerelease tokens</a>.
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
        return new Modifier(this).nextMajor();
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
        return new Modifier(this).withIncMajor(number);
    }

    /**
     * Increments minor to the next closest version.
     *
     * @return new incremented semver
     */
    public Semver nextMinor() {
        return new Modifier(this).nextMinor();
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
        return new Modifier(this).withIncMinor(number);
    }

    /**
     * Increments patch to the next closest version.
     *
     * @return new incremented semver
     */
    public Semver nextPatch() {
        return new Modifier(this).nextPatch();
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
        return new Modifier(this).withIncPatch(number);
    }

    /**
     * Sets pre-release version.
     *
     * @param preRelease version to set
     * @return semver with new pre-release
     */
    public Semver withPreRelease(String preRelease) {
        return new Modifier(this).withPreRelease(preRelease);
    }

    /**
     * Sets build version.
     *
     * @param build version to set
     * @return semver with new build
     */
    public Semver withBuild(String build) {
        return new Modifier(this).withBuild(build);
    }

    /**
     * Removes pre-release from semver.
     *
     * @return semver without pre-release
     */
    public Semver withClearedPreRelease() {
        return new Modifier(this).withClearedPreRelease();
    }

    /**
     * Removes build from semver.
     *
     * @return semver without build
     */
    public Semver withClearedBuild() {
        return new Modifier(this).withClearedBuild();
    }

    /**
     * Removes both pre-release and build from semver.
     *
     * @return semver without pre-release and build
     */
    public Semver withClearedPreReleaseAndBuild() {
        return new Modifier(this).withClearedPreReleaseAndBuild();
    }

    @Override
    public int compareTo(Semver other) {
        return new Comparator(this).compareTo(other);
    }

    /**
     * @param version the version to compare
     * @return true if the current version is greater than the provided version
     * @see #isGreaterThan(Semver)
     */
    public boolean isGreaterThan(String version) {
        return isGreaterThan(new Semver(version));
    }

    /**
     * Checks if the version is greater than another version
     *
     * @param version the version to compare
     * @return true if the current version is greater than the provided version
     */
    public boolean isGreaterThan(Semver version) {
        return compareTo(version) > 0;
    }

    /**
     * @param version the version to compare
     * @return true if the current version is greater than or equal to the provided version
     * @see #isGreaterThanOrEqualTo(Semver)
     */
    public boolean isGreaterThanOrEqualTo(String version) {
        return isGreaterThanOrEqualTo(new Semver(version));
    }

    /**
     * Checks if the version is greater than or equal to another version
     *
     * @param version the version to compare
     * @return true if the current version is greater than or equal to the provided version
     */
    public boolean isGreaterThanOrEqualTo(Semver version) {
        return compareTo(version) >= 0;
    }

    /**
     * @param version the version to compare
     * @return true if the current version is lower than the provided version
     * @see #isLowerThan(Semver)
     */
    public boolean isLowerThan(String version) {
        return isLowerThan(new Semver(version, this.type));
    }

    /**
     * Checks if the version is lower than another version
     *
     * @param version the version to compare
     * @return true if the current version is lower than the provided version
     */
    public boolean isLowerThan(Semver version) {
        return compareTo(version) < 0;
    }

    /**
     * @param version the version to compare
     * @return true if the current version is lower than or equal to the provided version
     * @see #isLowerThanOrEqualTo(Semver)
     */
    public boolean isLowerThanOrEqualTo(String version) {
        return isLowerThanOrEqualTo(new Semver(version));
    }

    /**
     * Checks if the version is lower than or equal to another version
     *
     * @param version the version to compare
     * @return true if the current version is lower than or equal to the provided version
     */
    public boolean isLowerThanOrEqualTo(Semver version) {
        return compareTo(version) <= 0;
    }

    /**
     * Checks if the version equals (exact compare) another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version, {@code false} otherwise
     * @see #isEqualTo(Semver)
     */
    public boolean isEqualTo(String version) {
        return isEqualTo(new Semver(version));
    }

    /**
     * Checks if the version equals (exact compare) another version.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version, {@code false} otherwise
     * @see #isEqualTo(String)
     */
    public boolean isEqualTo(Semver version) {
        return equals(version);
    }

    /**
     * Checks if the version equals another version, without taking the build into account.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version (build excluded), {@code false} otherwise
     * @see #isEquivalentTo(Semver)
     */
    public boolean isEquivalentTo(String version) {
        return isEquivalentTo(new Semver(version));
    }

    /**
     * Checks if the version equals another version, without taking the build into account.
     *
     * @param version version to compare
     * @return {@code true} if the current version equals the provided version (build excluded), {@code false} otherwise
     * @see #isEquivalentTo(String)
     */
    public boolean isEquivalentTo(Semver version) {
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
    public VersionDiff diff(String version) {
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
    public VersionDiff diff(Semver version) {
        return new Differ(this).diff(version);
    }

    /**
     * Check if the version satisfies a range.
     *
     * @param range range
     * @return {@code true} if the version satisfies the range, {@code false} otherwise
     * @see #satisfies(RangesList)
     */
    public boolean satisfies(String range) {
        RangesList rangesList = RangesListFactory.create(range);
        return satisfies(rangesList);
    }

    /**
     * Check if the version satisfies a ranges list.
     *
     * @param rangesList list with the ranges
     * @return {@code true} if the version satisfies the ranges list, {@code false} otherwise
     * @see #satisfies(String)
     */
    public boolean satisfies(RangesList rangesList) {
        return rangesList.isSatisfiedBy(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Semver semver = (Semver) o;
        return Objects.equals(version, semver.version);
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
     * The types of diffs between two versions.
     */
    public enum VersionDiff {
        NONE,
        MAJOR,
        MINOR,
        PATCH,
        SUFFIX,
        BUILD,
    }

    /**
     * The different types of supported version systems.
     */
    public enum SemverType {
        /**
         * <p>The default type of version.</p>
         * Interpret version strictly.
         * Format must be valid due to <a href="https://semver.org/#backusnaur-form-grammar-for-valid-semver-versions">specification</a>.
         */
        STRICT,
    }
}
