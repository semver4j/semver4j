package org.semver4j;

import org.semver4j.parsers.OtherSemverParser;
import org.semver4j.parsers.ParsedVersion;
import org.semver4j.parsers.SemverParser;
import org.semver4j.parsers.StrictSemverParser;

import java.util.Objects;

import static java.lang.String.format;
import static org.semver4j.Semver.SemverType.NPM;
import static org.semver4j.Semver.SemverType.STRICT;

/**
 * Semver is a tool that provides useful methods to manipulate versions that follow the "semantic versioning" specification
 * (see <a href="http://semver.org">semver.org</a>)
 */
public class Semver implements Comparable<Semver> {
    private final String originalValue;
    private final SemverType type;
    private final String value;

    private final int major;
    private final Integer minor;
    private final Integer patch;
    private final String[] suffixTokens;
    private final String build;

    public Semver(String version) {
        this(version, STRICT);
    }

    public Semver(String version, SemverType type) {
        this.originalValue = version;
        this.type = type;

        version = version.trim();
        if (type == NPM && (version.startsWith("v") || version.startsWith("V"))) {
            version = version.substring(1).trim();
        }
        this.value = version;

        SemverParser semverParser = type == STRICT ? new StrictSemverParser() : new OtherSemverParser(type);
        ParsedVersion parsedVersion = semverParser.parse(version);

        major = parsedVersion.getMajor();
        minor = parsedVersion.getMinor();
        patch = parsedVersion.getPatch();
        String preRelease = parsedVersion.getPreRelease();
        suffixTokens = preRelease == null ? new String[0] : preRelease.split("\\.");
        build = parsedVersion.getBuild();
    }

    /**
     * Check if the version satisfies a requirement.
     *
     * @param requirement the requirement
     * @return true if the version satisfies the requirement
     */
    public boolean satisfies(Requirement requirement) {
        return requirement.isSatisfiedBy(this);
    }

    /**
     * Check if the version satisfies a requirement.
     *
     * @param requirement the requirement
     * @return true if the version satisfies the requirement
     */
    public boolean satisfies(String requirement) {
        Requirement req;
        switch (type) {
            case STRICT:
                req = Requirement.buildStrict(requirement);
                break;
            case LOOSE:
                req = Requirement.buildLoose(requirement);
                break;
            case NPM:
                req = Requirement.buildNPM(requirement);
                break;
            case COCOAPODS:
                req = Requirement.buildCocoapods(requirement);
                break;
            case IVY:
                req = Requirement.buildIvy(requirement);
                break;
            default:
                throw new SemverException(format("Invalid requirement type: %s", type));
        }
        return this.satisfies(req);
    }

    /**
     * @param version the version to compare
     * @return true if the current version is greater than the provided version
     * @see #isGreaterThan(Semver)
     */
    public boolean isGreaterThan(String version) {
        return this.isGreaterThan(new Semver(version, this.getType()));
    }

    /**
     * Checks if the version is greater than another version
     *
     * @param version the version to compare
     * @return true if the current version is greater than the provided version
     */
    public boolean isGreaterThan(Semver version) {
        // Compare the main part
        if (this.getMajor() > version.getMajor()) return true;
        else if (this.getMajor() < version.getMajor()) return false;

        if (this.type == SemverType.NPM && version.getMinor() == null) return false;

        int otherMinor = version.getMinor() != null ? version.getMinor() : 0;
        if (this.getMinor() != null && this.getMinor() > otherMinor) return true;
        else if (this.getMinor() != null && this.getMinor() < otherMinor) return false;

        if (this.type == SemverType.NPM && version.getPatch() == null) return false;

        int otherPatch = version.getPatch() != null ? version.getPatch() : 0;
        if (this.getPatch() != null && this.getPatch() > otherPatch) return true;
        else if (this.getPatch() != null && this.getPatch() < otherPatch) return false;

        // Let's take a look at the suffix
        String[] tokens1 = this.getSuffixTokens();
        String[] tokens2 = version.getSuffixTokens();

        // If one of the versions has no suffix, it's greater!
        if (tokens1.length == 0 && tokens2.length > 0) return true;
        if (tokens2.length == 0 && tokens1.length > 0) return false;

        // Let's see if one of suffixes is greater than the other
        int i = 0;
        while (i < tokens1.length && i < tokens2.length) {
            int cmp;
            try {
                // Trying to resolve the suffix part with an integer
                int t1 = Integer.valueOf(tokens1[i]);
                int t2 = Integer.valueOf(tokens2[i]);
                cmp = t1 - t2;
            } catch (NumberFormatException e) {
                // Else, do a string comparison
                cmp = tokens1[i].compareToIgnoreCase(tokens2[i]);
            }
            if (cmp < 0) return false;
            else if (cmp > 0) return true;
            i++;
        }

        // If one of the versions has some remaining suffixes, it's greater
        return tokens1.length > tokens2.length;
    }

    /**
     * @param version the version to compare
     * @return true if the current version is greater than or equal to the provided version
     * @see #isGreaterThanOrEqualTo(Semver)
     */
    public boolean isGreaterThanOrEqualTo(String version) {
        return this.isGreaterThanOrEqualTo(new Semver(version, this.type));
    }

    /**
     * Checks if the version is greater than or equal to another version
     *
     * @param version the version to compare
     * @return true if the current version is greater than or equal to the provided version
     */
    public boolean isGreaterThanOrEqualTo(Semver version) {
        return this.isGreaterThan(version) || this.isEquivalentTo(version);
    }

    /**
     * @param version the version to compare
     * @return true if the current version is lower than the provided version
     * @see #isLowerThan(Semver)
     */
    public boolean isLowerThan(String version) {
        return this.isLowerThan(new Semver(version, this.type));
    }

    /**
     * Checks if the version is lower than another version
     *
     * @param version the version to compare
     * @return true if the current version is lower than the provided version
     */
    public boolean isLowerThan(Semver version) {
        return !this.isGreaterThan(version) && !this.isEquivalentTo(version);
    }

    /**
     * @param version the version to compare
     * @return true if the current version is lower than or equal to the provided version
     * @see #isLowerThanOrEqualTo(Semver)
     */
    public boolean isLowerThanOrEqualTo(String version) {
        return this.isLowerThanOrEqualTo(new Semver(version, this.type));
    }

    /**
     * Checks if the version is lower than or equal to another version
     *
     * @param version the version to compare
     * @return true if the current version is lower than or equal to the provided version
     */
    public boolean isLowerThanOrEqualTo(Semver version) {
        return !this.isGreaterThan(version);
    }

    /**
     * @param version the version to compare
     * @return true if the current version equals the provided version (build excluded)
     * @see #isEquivalentTo(Semver)
     */
    public boolean isEquivalentTo(String version) {
        return this.isEquivalentTo(new Semver(version, this.type));
    }

    /**
     * Checks if the version equals another version, without taking the build into account.
     *
     * @param version the version to compare
     * @return true if the current version equals the provided version (build excluded)
     */
    public boolean isEquivalentTo(Semver version) {
        // Get versions without build
        Semver sem1 = this.getBuild() == null ? this : new Semver(this.getValue().replace("+" + this.getBuild(), ""));
        Semver sem2 = version.getBuild() == null ? version : new Semver(version.getValue().replace("+" + version.getBuild(), ""));
        // Compare those new versions
        return sem1.isEqualTo(sem2);
    }

    /**
     * @param version the version to compare
     * @return true if the current version equals the provided version
     * @see #isEqualTo(Semver)
     */
    public boolean isEqualTo(String version) {
        return this.isEqualTo(new Semver(version, this.type));
    }

    /**
     * Checks if the version equals another version
     *
     * @param version the version to compare
     * @return true if the current version equals the provided version
     */
    public boolean isEqualTo(Semver version) {
        if (this.type == SemverType.NPM) {
            if (this.getMajor() != version.getMajor()) return false;
            if (version.getMinor() == null) return true;
            if (version.getPatch() == null) return true;
        }

        return this.equals(version);
    }

    /**
     * Determines if the current version is stable or not.
     * Stable version have a major version number strictly positive and no suffix tokens.
     *
     * @return true if the current version is stable
     */
    public boolean isStable() {
        return (this.getMajor() > 0) &&
                (this.getSuffixTokens() == null || this.getSuffixTokens().length == 0);
    }

    /**
     * @param version the version to compare
     * @return the greatest difference
     * @see #diff(Semver)
     */
    public VersionDiff diff(String version) {
        return this.diff(new Semver(version, this.type));
    }

    /**
     * Returns the greatest difference between 2 versions.
     * For example, if the current version is "1.2.3" and compared version is "1.3.0", the biggest difference
     * is the 'MINOR' number.
     *
     * @param version the version to compare
     * @return the greatest difference
     */
    public VersionDiff diff(Semver version) {
        if (!Objects.equals(this.major, version.getMajor())) return VersionDiff.MAJOR;
        if (!Objects.equals(this.minor, version.getMinor())) return VersionDiff.MINOR;
        if (!Objects.equals(this.patch, version.getPatch())) return VersionDiff.PATCH;
        if (!areSameSuffixes(version.getSuffixTokens())) return VersionDiff.SUFFIX;
        if (!Objects.equals(this.build, version.getBuild())) return VersionDiff.BUILD;
        return VersionDiff.NONE;
    }

    private boolean areSameSuffixes(String[] suffixTokens) {
        if (this.suffixTokens == null && suffixTokens == null) return true;
        else if (this.suffixTokens == null || suffixTokens == null) return false;
        else if (this.suffixTokens.length != suffixTokens.length) return false;
        for (int i = 0; i < this.suffixTokens.length; i++) {
            if (!this.suffixTokens[i].equals(suffixTokens[i])) return false;
        }
        return true;
    }

    public Semver toStrict() {
        Integer minor = this.minor != null ? this.minor : 0;
        Integer patch = this.patch != null ? this.patch : 0;
        return Semver.create(STRICT, this.major, minor, patch, this.suffixTokens, this.build);
    }

    public Semver withIncMajor() {
        return this.withIncMajor(1);
    }

    public Semver withIncMajor(int increment) {
        return this.withInc(increment, 0, 0);
    }

    public Semver withIncMinor() {
        return this.withIncMinor(1);
    }

    public Semver withIncMinor(int increment) {
        return this.withInc(0, increment, 0);
    }

    public Semver withIncPatch() {
        return this.withIncPatch(1);
    }

    public Semver withIncPatch(int increment) {
        return this.withInc(0, 0, increment);
    }

    private Semver withInc(int majorInc, int minorInc, int patchInc) {
        Integer minor = this.minor;
        Integer patch = this.patch;
        if (this.minor != null) {
            minor += minorInc;
        }
        if (this.patch != null) {
            patch += patchInc;
        }
        return with(this.major + majorInc, minor, patch, true, true);
    }

    public Semver withClearedSuffix() {
        return with(this.major, this.minor, this.patch, false, true);
    }

    public Semver withClearedBuild() {
        return with(this.major, this.minor, this.patch, true, false);
    }

    public Semver withClearedSuffixAndBuild() {
        return with(this.major, this.minor, this.patch, false, false);
    }

    public Semver withSuffix(String suffix) {
        return with(this.major, this.minor, this.patch, suffix.split("\\."), this.build);
    }

    public Semver withBuild(String build) {
        return with(this.major, this.minor, this.patch, this.suffixTokens, build);
    }

    public Semver nextMajor() {
        return with(this.major + 1, 0, 0, false, false);
    }

    public Semver nextMinor() {
        return with(this.major, this.minor + 1, 0, false, false);
    }

    public Semver nextPatch() {
        return with(this.major, this.minor, this.patch + 1, false, false);
    }

    private Semver with(int major, Integer minor, Integer patch, boolean suffix, boolean build) {
        minor = this.minor != null ? minor : null;
        patch = this.patch != null ? patch : null;
        String buildStr = build ? this.build : null;
        String[] suffixTokens = suffix ? this.suffixTokens : null;
        return Semver.create(this.type, major, minor, patch, suffixTokens, buildStr);
    }

    private Semver with(int major, Integer minor, Integer patch, String[] suffixTokens, String build) {
        minor = this.minor != null ? minor : null;
        patch = this.patch != null ? patch : null;
        return Semver.create(this.type, major, minor, patch, suffixTokens, build);
    }

    private static Semver create(SemverType type, int major, Integer minor, Integer patch, String[] suffix, String build) {
        StringBuilder sb = new StringBuilder()
                .append(major);
        if (minor != null) {
            sb.append(".").append(minor);
        }
        if (patch != null) {
            sb.append(".").append(patch);
        }
        if (suffix != null) {
            boolean first = true;
            for (String suffixToken : suffix) {
                if (first) {
                    sb.append("-");
                    first = false;
                } else {
                    sb.append(".");
                }
                sb.append(suffixToken);
            }
        }
        if (build != null) {
            sb.append("+").append(build);
        }

        return new Semver(sb.toString(), type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Semver)) return false;
        Semver version = (Semver) o;
        return value.equals(version.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public int compareTo(Semver version) {
        if (this.isGreaterThan(version)) return 1;
        else if (this.isLowerThan(version)) return -1;
        return 0;
    }

    @Override
    public String toString() {
        return getValue();
    }

    /**
     * Get the original value as a string
     *
     * @return the original string passed in the constructor
     */
    public String getOriginalValue() {
        return originalValue;
    }

    /**
     * Returns the version as a String
     *
     * @return the version as a String
     */
    public String getValue() {
        return value;
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
    public Integer getMinor() {
        return minor;
    }

    /**
     * Returns the patch part of the version.
     * Example: for "1.2.3" = 3
     *
     * @return the patch part of the version
     */
    public Integer getPatch() {
        return patch;
    }

    /**
     * Returns the suffix of the version.
     * Example: for "1.2.3-beta.4+sha98450956" = {"beta", "4"}
     *
     * @return the suffix of the version
     */
    public String[] getSuffixTokens() {
        return suffixTokens;
    }

    /**
     * Returns the build of the version.
     * Example: for "1.2.3-beta.4+sha98450956" = "sha98450956"
     *
     * @return the build of the version
     */
    public String getBuild() {
        return build;
    }

    public SemverType getType() {
        return type;
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
         * Major, minor and patch parts are <b>required</b>.<br>
         * Suffixes and build are optional.
         */
        STRICT,

        /**
         * Major part is <b>required</b>.<br>
         * Minor, patch, suffixes and build are optional.
         */
        LOOSE,

        /**
         * <p>Follows the rules of NPM.</p>
         * Supports ^, x, *, ~, and more.<br>
         * See <a href="https://github.com/npm/node-semver">node-semver</a>
         */
        NPM,

        /**
         * <p>Follows the rules of Cocoapods.</p>
         * Supports optimistic and comparison operators.<br>
         * See <a href="https://guides.cocoapods.org/using/the-podfile.html">CocoaPods</a>
         */
        COCOAPODS,

        /**
         * <p>Follows the rules of ivy.</p>
         * Supports dynamic parts (eg: 4.2.+) and ranges.<br>
         * See <a href="http://ant.apache.org/ivy/history/latest-milestone/ivyfile/dependency.html">Ivy files</a>
         */
        IVY,
    }
}
