package org.semver4j.internal;

import org.jspecify.annotations.NullMarked;
import org.semver4j.Semver;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

@NullMarked
public class Modifier {
    private static final String FULL_FORMAT = "%d.%d.%d";
    private static final String MAJOR_FORMAT = "%d.0.0";
    private static final String MAJOR_MINOR_FORMAT = "%d.%d.0";

    private Modifier() {
    }

    public static Semver nextMajor(final Semver version) {
        int nextMajor = version.getMajor();

        // Prerelease version 1.0.0-5 bumps to 1.0.0
        if (version.getMinor() != 0 || version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMajor = nextMajor + 1;
        }

        String newVersion = createFullVersion(version, format(Locale.ROOT, MAJOR_FORMAT, nextMajor), emptyList());
        return new Semver(newVersion);
    }

    public static Semver withIncMajor(final Semver version, int number) {
        String newVersion = createFullVersion(version, format(Locale.ROOT, FULL_FORMAT, (version.getMajor() + number), version.getMinor(), version.getPatch()), version.getPreRelease());
        return new Semver(newVersion);
    }

    public static Semver nextMinor(final Semver version) {
        int nextMinor = version.getMinor();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMinor = nextMinor + 1;
        }

        String newVersion = createFullVersion(version, format(Locale.ROOT, MAJOR_MINOR_FORMAT, version.getMajor(), nextMinor), emptyList());
        return new Semver(newVersion);
    }

    public static Semver withIncMinor(final Semver version, int number) {
        String newVersion = createFullVersion(version, format(Locale.ROOT, FULL_FORMAT, version.getMajor(), (version.getMinor() + number), version.getPatch()), version.getPreRelease());
        return new Semver(newVersion);
    }

    public static Semver nextPatch(final Semver version) {
        int newPatch = version.getPatch();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPreRelease().isEmpty()) {
            newPatch = newPatch + 1;
        }

        String newVersion = createFullVersion(version, format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), newPatch), emptyList());
        return new Semver(newVersion);
    }

    public static Semver withIncPatch(final Semver version, int number) {
        String newVersion = createFullVersion(version, format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), (version.getPatch() + number)), version.getPreRelease());
        return new Semver(newVersion);
    }

    public static Semver withPreRelease(final Semver version, final String preRelease) {
        List<String> newPreRelease = asList(preRelease.split("\\."));

        String newVersion = createFullVersion(version, format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()), newPreRelease);
        return new Semver(newVersion);
    }

    public static Semver withBuild(final Semver version, final String build) {
        List<String> newBuild = asList(build.split("\\."));

        String newVersion = createFullVersion(format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()), version.getPreRelease(), newBuild);
        return new Semver(newVersion);
    }

    public static Semver withClearedPreRelease(final Semver version) {
        String newVersion = createFullVersion(version, format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()), emptyList());
        return new Semver(newVersion);
    }

    public static Semver withClearedBuild(final Semver version) {
        String newVersion = createFullVersion(format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()), version.getPreRelease(), emptyList());
        return new Semver(newVersion);
    }

    public static Semver withClearedPreReleaseAndBuild(final Semver version) {
        String newVersion = format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch());
        return new Semver(newVersion);
    }

    private static String createFullVersion(
            final Semver version,
            final String main,
            final List<String> preRelease
    ) {
        return createFullVersion(main, preRelease, version.getBuild());
    }

    private static String createFullVersion(
            final String main,
            final List<String> preRelease,
            final List<String> build
    ) {
        StringBuilder stringBuilder = new StringBuilder(main);

        if (!preRelease.isEmpty()) {
            stringBuilder.append("-");
            for (String s : preRelease) {
                stringBuilder
                    .append(s)
                    .append(".");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }

        if (!build.isEmpty()) {
            stringBuilder.append("+");
            for (String s : build) {
                stringBuilder
                    .append(s)
                    .append(".");
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }
}
