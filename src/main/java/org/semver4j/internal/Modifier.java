package org.semver4j.internal;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Locale;
import org.semver4j.Semver;

/**
 * Utility class for semantic versioning operations. Provides methods to manipulate and transform {@link Semver}
 * instances according to semantic versioning specifications.
 */
public class Modifier {
    private static final String FULL_FORMAT = "%d.%d.%d";
    private static final String MAJOR_FORMAT = "%d.0.0";
    private static final String MAJOR_MINOR_FORMAT = "%d.%d.0";

    /** Private constructor to prevent instantiation of utility class. */
    private Modifier() {}

    /**
     * Increments the {@code major} version component.
     *
     * <p>If the version is a {@code pre-release} (e.g., {@code 1.0.0-5}), it will become a regular release
     * ({@code 1.0.0}). Otherwise, the {@code major} version is incremented and {@code minor} and {@code patch} are
     * reset to {@code 0}.
     *
     * @param version the version to increment
     * @return a new {@link Semver} instance with incremented {@code major} version
     */
    public static Semver nextMajor(Semver version) {
        int nextMajor = version.getMajor();

        // Prerelease version 1.0.0-5 bumps to 1.0.0
        if (version.getMinor() != 0
                || version.getPatch() != 0
                || version.getPreRelease().isEmpty()) {
            nextMajor = nextMajor + 1;
        }

        String newVersion = createFullVersion(version, format(Locale.ROOT, MAJOR_FORMAT, nextMajor), emptyList());
        return new Semver(newVersion);
    }

    /**
     * Increases the {@code major} version component by a specified number.
     *
     * @param version the version to modify
     * @param number the number to add to the major version
     * @return a new {@link Semver} instance with increased {@code major} version
     */
    public static Semver withIncMajor(Semver version, int number) {
        String newVersion = createFullVersion(
                version,
                format(Locale.ROOT, FULL_FORMAT, (version.getMajor() + number), version.getMinor(), version.getPatch()),
                version.getPreRelease());
        return new Semver(newVersion);
    }

    /**
     * Increments the {@code minor} version component.
     *
     * <p>If the version is a {@code pre-release} (e.g., {@code 1.2.0-5}), it will become a regular release
     * ({@code 1.2.0}). Otherwise, the {@code minor} version is incremented and {@code patch} is reset to {@code 0}.
     *
     * @param version the version to increment
     * @return a new {@link Semver} instance with incremented {@code minor} version
     */
    public static Semver nextMinor(Semver version) {
        int nextMinor = version.getMinor();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMinor = nextMinor + 1;
        }

        String newVersion = createFullVersion(
                version, format(Locale.ROOT, MAJOR_MINOR_FORMAT, version.getMajor(), nextMinor), emptyList());
        return new Semver(newVersion);
    }

    /**
     * Increases the {@code minor} version component by a specified number.
     *
     * @param version the version to modify
     * @param number the number to add to the {@code minor} version
     * @return a new {@link Semver} instance with increased {@code minor} version
     */
    public static Semver withIncMinor(Semver version, int number) {
        String newVersion = createFullVersion(
                version,
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), (version.getMinor() + number), version.getPatch()),
                version.getPreRelease());
        return new Semver(newVersion);
    }

    /**
     * Increments the {@code patch} version component.
     *
     * <p>If the version is a {@code pre-release} (e.g., {@code 1.2.3-5}), it will become a regular release
     * ({@code 1.2.3}). Otherwise, the {@code patch} version is incremented.
     *
     * @param version the version to increment
     * @return a new {@link Semver} instance with incremented {@code patch} version
     */
    public static Semver nextPatch(Semver version) {
        int newPatch = version.getPatch();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPreRelease().isEmpty()) {
            newPatch = newPatch + 1;
        }

        String newVersion = createFullVersion(
                version,
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), newPatch),
                emptyList());
        return new Semver(newVersion);
    }

    /**
     * Increases the {@code patch} version component by a specified number.
     *
     * @param version the version to modify
     * @param number the number to add to the {@code patch} version
     * @return a new {@link Semver} instance with increased {@code patch} version
     */
    public static Semver withIncPatch(Semver version, int number) {
        String newVersion = createFullVersion(
                version,
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), (version.getPatch() + number)),
                version.getPreRelease());
        return new Semver(newVersion);
    }

    /**
     * Sets the {@code pre-release} identifier of a version.
     *
     * @param version the version to modify
     * @param preRelease the {@code pre-release} identifier to set (dot-separated)
     * @return a new {@link Semver} instance with the specified {@code pre-release} identifier
     */
    public static Semver withPreRelease(Semver version, String preRelease) {
        List<String> newPreRelease = asList(preRelease.split("\\."));

        String newVersion = createFullVersion(
                version,
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                newPreRelease);
        return new Semver(newVersion);
    }

    /**
     * Sets the {@code build} metadata of a version.
     *
     * @param version the version to modify
     * @param build the {@code build} metadata to set (dot-separated)
     * @return a new {@link Semver} instance with the specified {@code build} metadata
     */
    public static Semver withBuild(Semver version, String build) {
        List<String> newBuild = asList(build.split("\\."));

        String newVersion = createFullVersion(
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                version.getPreRelease(),
                newBuild);
        return new Semver(newVersion);
    }

    /**
     * Removes the {@code pre-release} identifier from a version.
     *
     * @param version the version to modify
     * @return a new {@link Semver} instance without {@code pre-release} identifier
     */
    public static Semver withClearedPreRelease(Semver version) {
        String newVersion = createFullVersion(
                version,
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                emptyList());
        return new Semver(newVersion);
    }

    /**
     * Removes the {@code build} metadata from a version.
     *
     * @param version the version to modify
     * @return a new {@link Semver} instance without {@code build} metadata
     */
    public static Semver withClearedBuild(Semver version) {
        String newVersion = createFullVersion(
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                version.getPreRelease(),
                emptyList());
        return new Semver(newVersion);
    }

    /**
     * Removes both {@code pre-release} identifier and build metadata from a version.
     *
     * @param version the version to modify
     * @return a new {@link Semver} instance without {@code pre-release} identifier and build metadata
     */
    public static Semver withClearedPreReleaseAndBuild(Semver version) {
        String newVersion =
                format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch());
        return new Semver(newVersion);
    }

    private static String createFullVersion(Semver version, String main, List<String> preRelease) {
        return createFullVersion(main, preRelease, version.getBuild());
    }

    private static String createFullVersion(String main, List<String> preRelease, List<String> build) {
        StringBuilder stringBuilder = new StringBuilder(main);

        if (!preRelease.isEmpty()) {
            stringBuilder.append("-");
            for (String s : preRelease) {
                stringBuilder.append(s).append(".");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        if (!build.isEmpty()) {
            stringBuilder.append("+");
            for (String s : build) {
                stringBuilder.append(s).append(".");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
