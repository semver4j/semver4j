package org.semver4j.internal;

import org.jetbrains.annotations.NotNull;
import org.semver4j.Semver;

import java.util.List;
import java.util.Locale;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

public class Modifier {
    @NotNull
    private final Semver version;

    public Modifier(@NotNull final Semver version) {
        this.version = version;
    }

    @NotNull
    public Semver nextMajor() {
        int nextMajor = version.getMajor();

        // Prerelease version 1.0.0-5 bumps to 1.0.0
        if (version.getMinor() != 0 || version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMajor = nextMajor + 1;
        }

        String version = createFullVersion(format(Locale.ROOT, "%d.0.0", nextMajor), emptyList());
        return new Semver(version);
    }

    @NotNull
    public Semver withIncMajor(int number) {
        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", (this.version.getMajor() + number), this.version.getMinor(), this.version.getPatch()), this.version.getPreRelease());
        return new Semver(version);
    }

    @NotNull
    public Semver nextMinor() {
        int nextMinor = version.getMinor();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPatch() != 0 || version.getPreRelease().isEmpty()) {
            nextMinor = nextMinor + 1;
        }

        String version = createFullVersion(format(Locale.ROOT, "%d.%d.0", this.version.getMajor(), nextMinor), emptyList());
        return new Semver(version);
    }

    @NotNull
    public Semver withIncMinor(int number) {
        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), (this.version.getMinor() + number), this.version.getPatch()), this.version.getPreRelease());
        return new Semver(version);
    }

    @NotNull
    public Semver nextPatch() {
        int newPatch = version.getPatch();

        // Prerelease version 1.2.0-5 bumps to 1.2.0
        if (version.getPreRelease().isEmpty()) {
            newPatch = newPatch + 1;
        }

        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), newPatch), emptyList());
        return new Semver(version);
    }

    @NotNull
    public Semver withIncPatch(int number) {
        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), (this.version.getPatch() + number)), this.version.getPreRelease());
        return new Semver(version);
    }

    @NotNull
    public Semver withPreRelease(@NotNull final String preRelease) {
        List<String> newPreRelease = asList(preRelease.split("\\."));

        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), this.version.getPatch()), newPreRelease);
        return new Semver(version);
    }

    @NotNull
    public Semver withBuild(@NotNull final String build) {
        List<String> newBuild = asList(build.split("\\."));

        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), this.version.getPatch()), this.version.getPreRelease(), newBuild);
        return new Semver(version);
    }

    @NotNull
    public Semver withClearedPreRelease() {
        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), this.version.getPatch()), emptyList());
        return new Semver(version);
    }

    @NotNull
    public Semver withClearedBuild() {
        String version = createFullVersion(format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), this.version.getPatch()), this.version.getPreRelease(), emptyList());
        return new Semver(version);
    }

    @NotNull
    public Semver withClearedPreReleaseAndBuild() {
        String version = format(Locale.ROOT, "%d.%d.%d", this.version.getMajor(), this.version.getMinor(), this.version.getPatch());
        return new Semver(version);
    }

    @NotNull
    private String createFullVersion(@NotNull final String main, @NotNull final List<@NotNull String> preRelease) {
        return createFullVersion(main, preRelease, version.getBuild());
    }

    @NotNull
    private String createFullVersion(@NotNull final String main, @NotNull final List<@NotNull String> preRelease, @NotNull final List<@NotNull String> build) {
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
