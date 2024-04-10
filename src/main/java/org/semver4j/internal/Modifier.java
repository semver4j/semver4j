package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class Modifier {
    private static final String FULL_FORMAT = "%d.%d.%d";
    private static final String MAJOR_FORMAT = "%d.0.0";
    private static final String MAJOR_MINOR_FORMAT = "%d.%d.0";

    private Modifier() {
    }

    @NotNull
    public static Semver nextMajor(@NotNull final Semver version) {
        int nextMajor = version.getMajor();

        // Prerelease version 1.0.0-5 bumps to 1.0.0
        if (version.getMinor() != 0 || version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMajor = nextMajor + 1;
        }

        return new Semver(createFullVersion(version, format(Locale.ROOT, MAJOR_FORMAT, nextMajor), emptyList()));
    }

    @NotNull
    public static Semver withIncMajor(@NotNull final Semver version, int number) {
        return new Semver(
                createFullVersion(
                        version,
                        format(
                                Locale.ROOT,
                                FULL_FORMAT,
                                (version.getMajor() + number),
                                version.getMinor(),
                                version.getPatch()
                        ),
                        version.getPreRelease())
        );
    }

    @NotNull
    public static Semver nextMinor(@NotNull final Semver version) {
        int nextMinor = version.getMinor();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMinor = nextMinor + 1;
        }

        return new Semver(
                createFullVersion(
                        version,
                        format(Locale.ROOT, MAJOR_MINOR_FORMAT, version.getMajor(), nextMinor),
                        emptyList()
                )
        );
    }

    @NotNull
    public static Semver withIncMinor(@NotNull final Semver version, int number) {
        return new Semver(
                createFullVersion(
                        version,
                        format(
                                Locale.ROOT,
                                FULL_FORMAT,
                                version.getMajor(),
                                (version.getMinor() + number),
                                version.getPatch()
                        ),
                        version.getPreRelease()
                )
        );
    }

    @NotNull
    public static Semver nextPatch(@NotNull final Semver version) {
        int newPatch = version.getPatch();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPreRelease().isEmpty()) {
            newPatch = newPatch + 1;
        }

        return new Semver(
                createFullVersion(
                        version,
                        format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), newPatch),
                        emptyList()
                )
        );
    }

    @NotNull
    public static Semver withIncPatch(@NotNull final Semver version, int number) {
        return new Semver(
                createFullVersion(
                        version,
                        format(
                                Locale.ROOT,
                                FULL_FORMAT,
                                version.getMajor(),
                                version.getMinor(),
                                (version.getPatch() + number)
                        ),
                        version.getPreRelease()
                )
        );
    }

    @NotNull
    public static Semver withPreRelease(@NotNull final Semver version, @NotNull final String preRelease) {
        List<String> newPreRelease = asList(preRelease.split("\\."));

        return new Semver(
                createFullVersion(
                        version,
                        format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                        newPreRelease
                )
        );
    }

    @NotNull
    public static Semver withBuild(@NotNull final Semver version, @NotNull final String build) {
        List<String> newBuild = asList(build.split("\\."));

        return new Semver(
                createFullVersion(
                        format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                        version.getPreRelease(),
                        newBuild
                )
        );
    }

    @NotNull
    public static Semver withClearedPreRelease(@NotNull final Semver version) {
        return new Semver(
                createFullVersion(
                        version,
                        format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                        emptyList()
                )
        );
    }

    @NotNull
    public static Semver withClearedBuild(@NotNull final Semver version) {
        return new Semver(
                createFullVersion(
                        format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()),
                        version.getPreRelease(),
                        emptyList()
                )
        );
    }

    @NotNull
    public static Semver withClearedPreReleaseAndBuild(@NotNull final Semver version) {
        return new Semver(format(Locale.ROOT, FULL_FORMAT, version.getMajor(), version.getMinor(), version.getPatch()));
    }

    @NotNull
    private static String createFullVersion(
            @NotNull final Semver version,
            @NotNull final String main,
            @NotNull final List<@NotNull String> preRelease
    ) {
        return createFullVersion(main, preRelease, version.getBuild());
    }

    @NotNull
    private static String createFullVersion(
            @NotNull final String main,
            @NotNull final List<@NotNull String> preRelease,
            @NotNull final List<@NotNull String> build
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
