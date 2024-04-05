package org.semver4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.semver4j.Semver.VersionDiff;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.Arrays.asList;
import static java.util.Collections.nCopies;
import static java.util.Collections.sort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.semver4j.Semver.VersionDiff.*;
import static org.semver4j.Semver.coerce;
import static org.semver4j.Semver.isValid;

class SemverTest {
    @ParameterizedTest
    @ValueSource(strings = {"1.Y.3", "1.2.Y", "1.1.1.1", "1.0.0+", "1.0.0-", "1.0.0-alpha..1", "1.0.0-001", "1.0.0-äöü", "1.2.3."})
    void shouldThrowExceptionWhenSemverIsNotValid(String version) {
        //when/then
        assertThatThrownBy(() -> new Semver(version))
                .isInstanceOf(SemverException.class)
                .hasMessage(format(Locale.ROOT, "Version [%s] is not valid semver.", version));
    }

    @Test
    void shouldParseValidSemverWithAllSections() {
        //given
        String version = "1.2.3-beta.11+sha.0nsfgkjkjsdf";

        //when
        Semver semver = new Semver(version);

        //then
        assertThat(semver.getMajor()).isEqualTo(1);
        assertThat(semver.getMinor()).isEqualTo(2);
        assertThat(semver.getPatch()).isEqualTo(3);
        assertThat(semver.getPreRelease()).containsExactly("beta", "11");
        assertThat(semver.getBuild()).containsExactly("sha", "0nsfgkjkjsdf");
    }

    @Test
    void shouldThrowExceptionWhenIsWithoutPatch() {
        //when/then
        assertThatThrownBy(() -> new Semver("1.2-beta.11+sha.0nsfgkjkjsdf"))
                .isInstanceOf(SemverException.class)
                .hasMessage("Version [1.2-beta.11+sha.0nsfgkjkjsdf] is not valid semver.");
    }

    @Test
    void shouldThrowExceptionWhenIsWithoutMinor() {
        //when/then
        assertThatThrownBy(() -> new Semver("1-beta.11+sha.0nsfgkjkjsdf"))
                .isInstanceOf(SemverException.class)
                .hasMessage("Version [1-beta.11+sha.0nsfgkjkjsdf] is not valid semver.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3+sHa.0nSFGKjkjsdf", "1.2.3"})
    void shouldCheckVersionIsStable(String version) {
        //given
        Semver semver = new Semver(version);

        //when
        boolean stable = semver.isStable();

        //then
        assertThat(stable).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"1.2.3-BETA.11+sHa.0nSFGKjkjsdf", "0.1.2+sHa.0nSFGKjkjsdf", "0.1.2"})
    void shouldCheckVersionIsNotStable(String version) {
        //given
        Semver semver = new Semver(version);

        //when
        boolean stable = semver.isStable();

        //then
        assertThat(stable).isFalse();
    }

    @ParameterizedTest
    @MethodSource("nextMajor")
    void shouldSetNextMajor(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.nextMajor();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> nextMajor() {
        return Stream.of(
                arguments("1.2.3", "2.0.0"),
                arguments("1.2.3-tag", "2.0.0"),
                arguments("1.2.3-4", "2.0.0"),
                arguments("1.2.3-alpha.0.beta", "2.0.0"),
                arguments("1.0.0-1", "1.0.0"),
                arguments("1.2.3+build.098", "2.0.0+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("incrementMajor")
    void shouldIncrementMajor(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withIncMajor();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> incrementMajor() {
        return Stream.of(
                arguments("1.2.3", "2.2.3"),
                arguments("1.2.3-tag", "2.2.3-tag"),
                arguments("1.2.3-4", "2.2.3-4"),
                arguments("1.2.3-alpha.0.beta", "2.2.3-alpha.0.beta"),
                arguments("1.0.0-1", "2.0.0-1"),
                arguments("1.2.3+build.098", "2.2.3+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("incrementMajorByNumber")
    void shouldIncrementMajorByNumber(String version, int number, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withIncMajor(number);

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> incrementMajorByNumber() {
        return Stream.of(
                arguments("1.2.3", 2, "3.2.3"),
                arguments("1.2.3-tag", 2, "3.2.3-tag"),
                arguments("1.2.3-4", 2, "3.2.3-4"),
                arguments("1.2.3-alpha.0.beta", 2, "3.2.3-alpha.0.beta"),
                arguments("1.0.0-1", 2, "3.0.0-1"),
                arguments("1.2.3+build.098", 2, "3.2.3+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("nextMinor")
    void shouldNextMinor(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.nextMinor();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> nextMinor() {
        return Stream.of(
                arguments("1.2.3", "1.3.0"),
                arguments("1.2.3-tag", "1.3.0"),
                arguments("1.2.3-4", "1.3.0"),
                arguments("1.2.3-alpha.0.beta", "1.3.0"),
                arguments("1.2.0-1", "1.2.0"),
                arguments("1.2.3+build.098", "1.3.0+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("incrementMinor")
    void shouldIncrementMinor(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withIncMinor();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> incrementMinor() {
        return Stream.of(
                arguments("1.2.3", "1.3.3"),
                arguments("1.2.3-tag", "1.3.3-tag"),
                arguments("1.2.3-4", "1.3.3-4"),
                arguments("1.2.3-alpha.0.beta", "1.3.3-alpha.0.beta"),
                arguments("1.2.0-1", "1.3.0-1"),
                arguments("1.2.3+build.098", "1.3.3+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("incrementMinorByNumber")
    void shouldIncrementMinorByNumber(String version, int number, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withIncMinor(number);

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> incrementMinorByNumber() {
        return Stream.of(
                arguments("1.2.3", 2, "1.4.3"),
                arguments("1.2.3-tag", 2, "1.4.3-tag"),
                arguments("1.2.3-4", 2, "1.4.3-4"),
                arguments("1.2.3-alpha.0.beta", 2, "1.4.3-alpha.0.beta"),
                arguments("1.2.0-1", 2, "1.4.0-1"),
                arguments("1.2.3+build.098", 2, "1.4.3+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("nextPatch")
    void shouldNextPatch(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.nextPatch();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> nextPatch() {
        return Stream.of(
                arguments("1.2.3", "1.2.4"),
                arguments("1.2.3-tag", "1.2.3"),
                arguments("1.2.3-4", "1.2.3"),
                arguments("1.2.3-alpha.0.beta", "1.2.3"),
                arguments("1.2.0-1", "1.2.0"),
                arguments("1.2.3+build.098", "1.2.4+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("incrementPatch")
    void shouldIncrementPatch(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withIncPatch();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> incrementPatch() {
        return Stream.of(
                arguments("1.2.3", "1.2.4"),
                arguments("1.2.3-tag", "1.2.4-tag"),
                arguments("1.2.3-4", "1.2.4-4"),
                arguments("1.2.3-alpha.0.beta", "1.2.4-alpha.0.beta"),
                arguments("1.2.0-1", "1.2.1-1"),
                arguments("1.2.3+build.098", "1.2.4+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("incrementPatchByNumber")
    void shouldIncrementPatchByNumber(String version, int number, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withIncPatch(number);

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> incrementPatchByNumber() {
        return Stream.of(
                arguments("1.2.3", 2, "1.2.5"),
                arguments("1.2.3-tag", 2, "1.2.5-tag"),
                arguments("1.2.3-4", 2, "1.2.5-4"),
                arguments("1.2.3-alpha.0.beta", 2, "1.2.5-alpha.0.beta"),
                arguments("1.2.0-1", 2, "1.2.2-1"),
                arguments("1.2.3+build.098", 2, "1.2.5+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("withPreRelease")
    void shouldReturnSemverWithPreRelease(String version, String preRelease, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withPreRelease(preRelease);

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> withPreRelease() {
        return Stream.of(
                arguments("1.2.3", "beta", "1.2.3-beta"),
                arguments("1.2.3", "beta.1", "1.2.3-beta.1"),
                arguments("1.2.3-old", "new", "1.2.3-new"),
                arguments("1.2.3+build.098", "alpha.1", "1.2.3-alpha.1+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("withBuild")
    void shouldReturnSemverWithBuild(String version, String build, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withBuild(build);

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> withBuild() {
        return Stream.of(
                arguments("1.2.3", "1234", "1.2.3+1234"),
                arguments("1.2.3", "sha.342t51", "1.2.3+sha.342t51"),
                arguments("1.2.3-alpha.1", "new", "1.2.3-alpha.1+new"),
                arguments("1.2.3+build.098", "build.100", "1.2.3+build.100")
        );
    }

    @ParameterizedTest
    @MethodSource("withClearedPreRelease")
    void shouldReturnSemverWithClearedPreRelease(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withClearedPreRelease();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> withClearedPreRelease() {
        return Stream.of(
                arguments("1.2.3", "1.2.3"),
                arguments("1.2.3-rc", "1.2.3"),
                arguments("1.2.3-alpha.1", "1.2.3"),
                arguments("1.2.3-beta.2+build.098", "1.2.3+build.098"),
                arguments("1.2.3+build.098", "1.2.3+build.098")
        );
    }

    @ParameterizedTest
    @MethodSource("withClearedBuild")
    void shouldReturnSemverWithClearedBuild(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withClearedBuild();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> withClearedBuild() {
        return Stream.of(
                arguments("1.2.3", "1.2.3"),
                arguments("1.2.3-rc", "1.2.3-rc"),
                arguments("1.2.3-beta.2+build.098", "1.2.3-beta.2"),
                arguments("1.2.3+build.098", "1.2.3")
        );
    }

    @ParameterizedTest
    @MethodSource("withClearedPreReleaseAndBuild")
    void shouldReturnSemverWithClearedPreReleaseAndBuild(String version, String expected) {
        //given
        Semver semver = new Semver(version);

        //when
        Semver actualSemver = semver.withClearedPreReleaseAndBuild();

        //then
        assertThat(actualSemver.getVersion()).isEqualTo(expected);
    }

    static Stream<Arguments> withClearedPreReleaseAndBuild() {
        return Stream.of(
                arguments("1.2.3", "1.2.3"),
                arguments("1.2.3-rc", "1.2.3"),
                arguments("1.2.3-beta.2+build.098", "1.2.3"),
                arguments("1.2.3+build.098", "1.2.3")
        );
    }

    @Test
    void shouldSortVersions() {
        //given
        List<Semver> semvers = asList(new Semver("1.2.3"), new Semver("1.2.3-rc3"),
                new Semver("1.2.3-rc2"), new Semver("1.2.3-rc1"), new Semver("1.2.2"),
                new Semver("1.2.2-rc2"), new Semver("1.2.2-rc1"), new Semver("1.2.0"),
                new Semver("1.2.2-beta.1")
        );

        //when
        sort(semvers);

        //then
        assertThat(semvers)
                .extracting(Semver::getVersion)
                .containsExactly("1.2.0", "1.2.2-beta.1", "1.2.2-rc1", "1.2.2-rc2", "1.2.2", "1.2.3-rc1", "1.2.3-rc2", "1.2.3-rc3", "1.2.3");
    }

    @ParameterizedTest
    @MethodSource("apiCompatible")
    void shouldCheckIsApiCompatible(String version, boolean expected) {
        //given
        Semver semver = new Semver("1.2.3");

        //when
        boolean apiCompatible = semver.isApiCompatible(version);

        //then
        assertThat(apiCompatible).isEqualTo(expected);
    }

    static Stream<Arguments> apiCompatible() {
        return Stream.of(
                arguments("0.4.3", false),
                arguments("1.1.9", true),
                arguments("1.2.0", true),
                arguments("1.2.3", true),
                arguments("1.2.4", true),
                arguments("1.4.2", true),
                arguments("2.0.0", false),
                arguments("2.2.3", false)
        );
    }

    @ParameterizedTest
    @MethodSource("greaterThan")
    void shouldCheckIsVersionGreaterThan(String version1, String version2, boolean expected) {
        //given
        Semver semver = new Semver(version1);

        //when
        boolean greaterThan = semver.isGreaterThan(version2);

        //then
        assertThat(greaterThan).isEqualTo(expected);
    }

    static Stream<Arguments> greaterThan() {
        return Stream.of(
                arguments("1.0.0-alpha.1", "1.0.0-alpha", true),
                arguments("1.0.0-alpha.beta", "1.0.0-alpha.1", true),
                arguments("1.0.0-beta", "1.0.0-alpha.beta", true),
                arguments("1.0.0-beta.2", "1.0.0-beta", true),
                arguments("1.0.0-beta.11", "1.0.0-beta.2", true),
                arguments("1.0.0-rc.1", "1.0.0-beta.11", true),
                arguments("1.0.0", "1.0.0-rc.1", true),
                arguments("1.0.0-rc11", "1.0.0-rc3", true),
                arguments("1.0.0-beta11", "1.0.0-beta3", true),
                arguments("1.0.0-rc.3.x-13", "1.0.0-rc.3.x-3", true),
                arguments("1.24.1-A-20240111143214", "1.24.1-A-20240111143213", true),

                arguments("1.0.0-alpha", "1.0.0-alpha.1", false),
                arguments("1.0.0-alpha.1", "1.0.0-alpha.beta", false),
                arguments("1.0.0-alpha.beta", "1.0.0-beta", false),
                arguments("1.0.0-beta", "1.0.0-beta.2", false),
                arguments("1.0.0-beta.2", "1.0.0-beta.11", false),
                arguments("1.0.0-beta.11", "1.0.0-rc.1", false),
                arguments("1.0.0-rc.1", "1.0.0", false),
                arguments("1.0.0", "1.0.0", false),
                arguments("1.0.0-alpha.12", "1.0.0-alpha.12", false),
                arguments("0.0.1", "5.0.0", false),
                arguments("1.0.0-alpha.12.ab-c", "1.0.0-alpha.12.ab-c", false),
                arguments("1.0.0-rc3", "1.0.0-rc11", false),
                arguments("1.0.0-beta11", "1.0.0-rc3", false),
                arguments("1.24.1-A-20240111143213", "1.24.1-A-20240111143214", false)
        );
    }

    @ParameterizedTest
    @MethodSource("greaterThanOrEqual")
    void shouldCheckIsVersionGreaterThanOrEqual(String version1, String version2, boolean expected) {
        //given
        Semver semver = new Semver(version1);

        //when
        boolean greaterThanOrEqualTo = semver.isGreaterThanOrEqualTo(version2);

        //then
        assertThat(greaterThanOrEqualTo).isEqualTo(expected);
    }

    static Stream<Arguments> greaterThanOrEqual() {
        return Stream.of(
                arguments("1.0.0-alpha.1", "1.0.0-alpha", true),
                arguments("1.0.0-alpha.beta", "1.0.0-alpha.1", true),
                arguments("1.0.0-beta", "1.0.0-alpha.beta", true),
                arguments("1.0.0-beta.2", "1.0.0-beta", true),
                arguments("1.0.0-beta.11", "1.0.0-beta.2", true),
                arguments("1.0.0-rc.1", "1.0.0-beta.11", true),
                arguments("1.0.0", "1.0.0-rc.1", true),
                arguments("1.0.0", "1.0.0", true),
                arguments("1.0.0-alpha.12", "1.0.0-alpha.12", true),
                arguments("1.0.0-alpha.12.ab-c", "1.0.0-alpha.12.ab-c", true),
                arguments("1.0.0-alpha.12.ab-c+sha.123", "1.0.0-alpha.12.ab-c+sha.123", true),
                arguments("1.0.0-alpha.12.ab-c+sha.123", "1.0.0-alpha.12.ab-c+sha.987", true),

                arguments("1.0.0-alpha", "1.0.0-alpha.1", false),
                arguments("1.0.0-alpha.1", "1.0.0-alpha.beta", false),
                arguments("1.0.0-alpha.beta", "1.0.0-beta", false),
                arguments("1.0.0-beta", "1.0.0-beta.2", false),
                arguments("1.0.0-beta.2", "1.0.0-beta.11", false),
                arguments("1.0.0-beta.11", "1.0.0-rc.1", false),
                arguments("1.0.0-rc.1", "1.0.0", false),
                arguments("0.0.1", "5.0.0", false)
        );
    }

    @ParameterizedTest
    @MethodSource("lowerThan")
    void shouldCheckIsVersionLowerThan(String version1, String version2, boolean expected) {
        //given
        Semver semver = new Semver(version1);

        //when
        boolean lowerThan = semver.isLowerThan(version2);

        //then
        assertThat(lowerThan).isEqualTo(expected);
    }

    static Stream<Arguments> lowerThan() {
        return Stream.of(
                arguments("1.0.0-alpha", "1.0.0-alpha.1", true),
                arguments("1.0.0-alpha.1", "1.0.0-alpha.beta", true),
                arguments("1.0.0-alpha.beta", "1.0.0-beta", true),
                arguments("1.0.0-beta", "1.0.0-beta.2", true),
                arguments("1.0.0-beta.2", "1.0.0-beta.11", true),
                arguments("1.0.0-beta.11", "1.0.0-rc.1", true),
                arguments("1.0.0-rc.1", "1.0.0", true),
                arguments("1.0.0-rc3", "1.0.0-rc11", true),
                arguments("1.0.0-rc.3.x-3", "1.0.0-rc.3.x-13", true),

                arguments("1.0.0-rc.3.x-13", "1.0.0-beta11", false),
                arguments("1.0.0", "1.0.0", false),
                arguments("1.0.0-alpha.12", "1.0.0-alpha.12", false),
                arguments("1.0.0-alpha.12.x-yz", "1.0.0-alpha.12.x-yz", false),
                arguments("1.0.0-alpha.1", "1.0.0-alpha", false),
                arguments("1.0.0-alpha.beta", "1.0.0-alpha.1", false),
                arguments("1.0.0-beta", "1.0.0-alpha.beta", false),
                arguments("1.0.0-beta.2", "1.0.0-beta", false),
                arguments("1.0.0-beta.11", "1.0.0-beta.2", false),
                arguments("1.0.0-rc.1", "1.0.0-beta.11", false),
                arguments("1.0.0", "1.0.0-rc.1", false)
        );
    }

    @ParameterizedTest
    @MethodSource("lowerThanOrEqual")
    void shouldCheckIsVersionLowerThanOrEqual(String version1, String version2, boolean expected) {
        //given
        Semver semver = new Semver(version1);

        //when
        boolean lowerThanOrEqualTo = semver.isLowerThanOrEqualTo(version2);

        //then
        assertThat(lowerThanOrEqualTo).isEqualTo(expected);
    }

    static Stream<Arguments> lowerThanOrEqual() {
        return Stream.of(
                arguments("1.0.0-alpha", "1.0.0-alpha.1", true),
                arguments("1.0.0-alpha.1", "1.0.0-alpha.beta", true),
                arguments("1.0.0-alpha.beta", "1.0.0-beta", true),
                arguments("1.0.0-beta", "1.0.0-beta.2", true),
                arguments("1.0.0-beta.2", "1.0.0-beta.11", true),
                arguments("1.0.0-beta.11", "1.0.0-rc.1", true),
                arguments("1.0.0-rc.1", "1.0.0", true),
                arguments("1.0.0", "1.0.0", true),
                arguments("1.0.0-alpha.12", "1.0.0-alpha.12", true),
                arguments("1.0.0-alpha.12.x-yz", "1.0.0-alpha.12.x-yz", true),

                arguments("1.0.0-alpha.1", "1.0.0-alpha", false),
                arguments("1.0.0-alpha.beta", "1.0.0-alpha.1", false),
                arguments("1.0.0-beta", "1.0.0-alpha.beta", false),
                arguments("1.0.0-beta.2", "1.0.0-beta", false),
                arguments("1.0.0-beta.11", "1.0.0-beta.2", false),
                arguments("1.0.0-rc.1", "1.0.0-beta.11", false),
                arguments("1.0.0", "1.0.0-rc.1", false)
        );
    }

    @ParameterizedTest
    @MethodSource("equivalent")
    void shouldCheckIsVersionEquivalent(String version1, String version2, boolean expected) {
        //given
        Semver semver = new Semver(version1);

        //when
        boolean equalTo = semver.isEquivalentTo(version2);

        //then
        assertThat(equalTo).isEqualTo(expected);
    }

    static Stream<Arguments> equivalent() {
        return Stream.of(
                arguments("1.0.0-alpha", "1.0.0-alpha.1", false),
                arguments("1.0.0-alpha.1", "1.0.0-alpha.beta", false),
                arguments("1.0.0-alpha.beta", "1.0.0-beta", false),
                arguments("1.0.0-beta", "1.0.0-beta.2", false),
                arguments("1.0.0-beta.2", "1.0.0-beta.11", false),
                arguments("1.0.0-beta.11", "1.0.0-rc.1", false),
                arguments("1.0.0-rc.1", "1.0.0", false),
                arguments("1.0.0", "1.2.0", false),
                arguments("1.0.0", "1.0.2", false),

                arguments("2021.1.6", "2021.1.6", true),
                arguments("1.0.0", "1.0.0", true),
                arguments("1.0.0-alpha.12", "1.0.0-alpha.12", true),
                arguments("1.0.0-alpha.12.x-yz", "1.0.0-alpha.12.x-yz", true),
                arguments("1.0.0-alpha.12.x-yz+sha.1", "1.0.0-alpha.12.x-yz+sha.sdhbfu3", true),
                arguments("1.0.0+sha.1", "1.0.0+sha.2", true)
        );
    }

    @ParameterizedTest
    @MethodSource("equal")
    void shouldCheckIsVersionEqual(String version1, String version2, boolean expected) {
        //given
        Semver semver = new Semver(version1);

        //when
        boolean equalTo = semver.isEqualTo(version2);

        //then
        assertThat(equalTo).isEqualTo(expected);
    }

    static Stream<Arguments> equal() {
        return Stream.of(
                arguments("1.0.0-alpha", "1.0.0-alpha.1", false),
                arguments("1.0.0-alpha.1", "1.0.0-alpha.beta", false),
                arguments("1.0.0-alpha.beta", "1.0.0-beta", false),
                arguments("1.0.0-beta", "1.0.0-beta.2", false),
                arguments("1.0.0-beta.2", "1.0.0-beta.11", false),
                arguments("1.0.0-beta.11", "1.0.0-rc.1", false),
                arguments("1.0.0-rc.1", "1.0.0", false),
                arguments("1.0.0", "1.2.0", false),
                arguments("1.0.0", "1.0.2", false),
                arguments("1.0.0+sha.1", "1.0.0+sha.2", false),
                arguments("1.0.0-alpha.12+sha.1", "1.0.0-alpha.12+sha.2", false),

                arguments("1.0.0", "1.0.0", true),
                arguments("1.0.0-alpha.12", "1.0.0-alpha.12", true),
                arguments("1.0.0-alpha.12.x-yz", "1.0.0-alpha.12.x-yz", true),
                arguments("1.0.0-alpha.12.x-yz+sha.1", "1.0.0-alpha.12.x-yz+sha.1", true)
        );
    }

    @ParameterizedTest
    @MethodSource("diff")
    void shouldReturnDiff(String version, VersionDiff expected) {
        //given
        Semver sem = new Semver("1.2.3-beta.4+sha899d8g79f87");

        //when
        VersionDiff diff = sem.diff(version);

        //then
        assertThat(diff).isEqualTo(expected);
    }

    static Stream<Arguments> diff() {
        return Stream.of(
                arguments("1.2.3-beta.4+sha899d8g79f87", NONE),
                arguments("2.3.4-alpha.5+sha32iddfu987", MAJOR),
                arguments("1.3.4-alpha.5+sha32iddfu987", MINOR),
                arguments("1.2.4-alpha.5+sha32iddfu987", PATCH),
                arguments("1.2.3-alpha.4+sha32iddfu987", PRE_RELEASE),
                arguments("1.2.3-beta.5+sha32iddfu987", PRE_RELEASE),
                arguments("1.2.3-beta.4+sha32iddfu987", BUILD),
                arguments("1.2.3-beta.4+sha899-d8g79f87", BUILD)
        );
    }

    @Test
    void shouldReturnNullWhenCannotParseVersion() {
        //when
        Semver version = Semver.parse("1.0");

        //then
        assertThat(version).isNull();
    }

    @Test
    void shouldReturnSemverWhenCanParseVersion() {
        //when
        Semver version = Semver.parse("1.0.0");

        //then
        assertThat(version).isInstanceOf(Semver.class);
    }

    @ParameterizedTest
    @MethodSource("coerceVersions")
    void shouldTryCoerceVersion(String versionToCoerce, String expected) {
        //when
        Semver semver = coerce(versionToCoerce);

        //then
        assertThat(semver.toString()).isEqualTo(expected);
    }

    static Stream<Arguments> coerceVersions() {
        return Stream.of(
                arguments(".1", "1.0.0"),
                arguments(".1.", "1.0.0"),
                arguments("..1", "1.0.0"),
                arguments(".1.1", "1.1.0"),
                arguments("1.", "1.0.0"),
                arguments("1.0", "1.0.0"),
                arguments("1.0.0", "1.0.0"),
                arguments("0", "0.0.0"),
                arguments("0.0", "0.0.0"),
                arguments("0.0.0", "0.0.0"),
                arguments("0.1", "0.1.0"),
                arguments("0.0.1", "0.0.1"),
                arguments("0.1.1", "0.1.1"),
                arguments("1", "1.0.0"),
                arguments("1.2", "1.2.0"),
                arguments("1.2.3", "1.2.3"),
                arguments("1.2.3.4", "1.2.3"),
                arguments("13", "13.0.0"),
                arguments("35.12", "35.12.0"),
                arguments("35.12.18", "35.12.18"),
                arguments("35.12.18.24", "35.12.18"),
                arguments("v1", "1.0.0"),
                arguments("v1.2", "1.2.0"),
                arguments("v1.2.3", "1.2.3"),
                arguments("v1.2.3.4", "1.2.3"),
                arguments(" 1", "1.0.0"),
                arguments("1 ", "1.0.0"),
                arguments("1 0", "1.0.0"),
                arguments("1 1", "1.0.0"),
                arguments("1.1 1", "1.1.0"),
                arguments("1.1-1", "1.1.0"),
                arguments("1.1-1", "1.1.0"),
                arguments("a1", "1.0.0"),
                arguments("a1a", "1.0.0"),
                arguments("1a", "1.0.0"),
                arguments("version 1", "1.0.0"),
                arguments("version1", "1.0.0"),
                arguments("version1.0", "1.0.0"),
                arguments("version1.1", "1.1.0"),
                arguments("42.6.7.9.3-alpha", "42.6.7"),
                arguments("v2", "2.0.0"),
                arguments("v3.4 replaces v3.3.1", "3.4.0"),
                arguments("4.6.3.9.2-alpha2", "4.6.3"),
                arguments(format(Locale.ROOT, "%s.2", repeat("1", 17)), "2.0.0"),
                arguments(format(Locale.ROOT, "%s.2.3", repeat("1", 17)), "2.3.0"),
                arguments(format(Locale.ROOT, "1.%s.3", repeat("2", 17)), "1.0.0"),
                arguments(format(Locale.ROOT, "1.2.%s", repeat("3", 17)), "1.2.0"),
                arguments(format(Locale.ROOT, "%s.2.3.4", repeat("1", 17)), "2.3.4"),
                arguments(format(Locale.ROOT, "1.%s.3.4", repeat("2", 17)), "1.0.0"),
                arguments(format(Locale.ROOT, "1.2.%s.4", repeat("3", 17)), "1.2.0"),
                arguments("10", "10.0.0"),
                arguments("3.2.1-rc.2", "3.2.1-rc.2")
        );
    }

    @Test
    void shouldReturnNullWhenTryCoerceNullableVersion() {
        //when
        Semver semver = coerce(null);

        //then
        assertThat(semver).isNull();
    }

    @Test
    void shouldReturnNullWhenVersionHasInvalidString() {
        //given
        String invalid = "broken-version";

        //when
        Semver semver = coerce(invalid);

        //then
        assertThat(semver).isNull();
    }

    @Test
    void shouldCreateSemverWithHyphenInBuildSection() {
        //when
        Semver semver = new Semver("1.2.3+123-abc");

        //then
        assertThat(semver.getMajor()).isEqualTo(1);
        assertThat(semver.getMinor()).isEqualTo(2);
        assertThat(semver.getPatch()).isEqualTo(3);
        assertThat(semver.getPreRelease()).isEmpty();
        assertThat(semver.getBuild()).containsExactly("123-abc");
    }

    @Test
    void shouldCreateSemverWithHyphenInPreReleaseSection() {
        //when
        Semver semver = new Semver("1.2.3-alpha-abc+123");

        //then
        assertThat(semver.getMajor()).isEqualTo(1);
        assertThat(semver.getMinor()).isEqualTo(2);
        assertThat(semver.getPatch()).isEqualTo(3);
        assertThat(semver.getPreRelease()).containsExactly("alpha-abc");
        assertThat(semver.getBuild()).containsExactly("123");
    }

    @Test
    void shouldSemverBeSymmetric() {
        //given
        Semver version1 = new Semver("2.10.1");
        Semver version2 = coerce("2.99");

        //when
        boolean equalTo1 = version1.isEqualTo(version2);
        boolean equalTo2 = version2.isEqualTo(version1);

        //then
        assertThat(equalTo1).isEqualTo(equalTo2);
    }

    @Test
    void shouldComparatorsBeSymmetric() {
        //given
        Semver version1 = coerce("2.0");
        Semver version2 = new Semver("2.0.0");

        //when
        boolean greaterThan1 = version1.isGreaterThan(version2);
        boolean greaterThan2 = version2.isGreaterThan(version1);

        boolean equivalentTo1 = version1.isEquivalentTo(version2);
        boolean equivalentTo2 = version2.isEquivalentTo(version1);

        boolean lowerThan1 = version1.isLowerThan(version2);
        boolean lowerThan2 = version2.isLowerThan(version1);

        //then
        assertThat(greaterThan1).isEqualTo(greaterThan2);

        assertThat(equivalentTo1).isEqualTo(equivalentTo2);

        assertThat(lowerThan1).isEqualTo(lowerThan2);
    }

    @ParameterizedTest
    @MethodSource("getParameters")
    void shouldCheckSatisfies(String version, String range, boolean expected) {
        //given
        Semver semver = new Semver(version);

        //when
        boolean satisfies = semver.satisfies(range);

        //then
        assertThat(satisfies).isEqualTo(expected);
    }

    static Stream<Arguments> getParameters() {
        return Stream.of(
                // Fully-qualified versions:
                arguments("1.0.0", "1.0.0", true),
                arguments("1.0.0", "=1.0.0", true),
                arguments("1.2.3", "1.2.3", true),
                arguments("1.2.4", "1.2.3", false),
                arguments("1.0.0-setup-20220428123901", "1.0.0-setup-20220428123901", true),

                // Minor versions:
                arguments("1.2.3", "1.2", true),
                arguments("1.2.4", "1.3", false),

                // Major versions:
                arguments("1.2.3", "1", true),
                arguments("1.2.4", "2", false),

                // Hyphen ranges:
                arguments("1.2.4-beta+exp.sha.5114f85", "1.2.3 - 2.3.4", false),
                arguments("1.2.4", "1.2.3 - 2.3.4", true),
                arguments("1.2.3", "1.2.3 - 2.3.4", true),
                arguments("2.3.4", "1.2.3 - 2.3.4", true),
                arguments("2.3.0-alpha", "1.2.3 - 2.3.0-beta", true),
                arguments("2.3.4", "1.2.3 - 2.3", true),
                arguments("2.3.4", "1.2.3 - 2", true),
                arguments("4.4.0", "3.X - 4.X", true),
                arguments("1.0.0", "1.2.3 - 2.3.4", false),
                arguments("3.0.0", "1.2.3 - 2.3.4", false),
                arguments("2.4.3", "1.2.3 - 2.3", false),
                arguments("2.3.0-rc1", "1.2.3 - 2.3.0-beta", false),
                arguments("3.0.0", "1.2.3 - 2", false),

                // Wildcard ranges:
                arguments("3.1.5", "", true),
                arguments("3.1.5", "*", true),
                arguments("0.0.0", "*", true),
                arguments("1.0.0-beta", "*", false),
                arguments("3.1.5-beta", "3.1.x", false),
                arguments("3.1.5-beta+exp.sha.5114f85", "3.1.x", false),
                arguments("3.1.5+exp.sha.5114f85", "3.1.x", true),
                arguments("3.1.5", "3.1.x", true),
                arguments("3.1.5", "3.1.X", true),
                arguments("3.1.5", "3.x", true),
                arguments("3.1.5", "3.*", true),
                arguments("3.1.5", "3.1", true),
                arguments("3.1.5", "3", true),
                arguments("3.2.5", "3.1.x", false),
                arguments("3.0.5", "3.1.x", false),
                arguments("4.0.0", "3.x", false),
                arguments("2.0.0", "3.x", false),
                arguments("3.2.5", "3.1", false),
                arguments("3.0.5", "3.1", false),
                arguments("4.0.0", "3", false),
                arguments("2.0.0", "3", false),

                // Tilde ranges:
                arguments("1.2.4-beta", "~1.2.3", false),
                arguments("1.2.4-beta+exp.sha.5114f85", "~1.2.3", false),
                arguments("1.2.3", "~1.2.3", true),
                arguments("1.2.7", "~1.2.3", true),
                arguments("1.2.2", "~1.2", true),
                arguments("1.2.0", "~1.2", true),
                arguments("1.3.0", "~1", true),
                arguments("1.0.0", "~1", true),
                arguments("1.2.3", "~1.2.3-beta.2", true),
                arguments("1.2.3-beta.4", "~1.2.3-beta.2", true),
                arguments("1.2.4", "~1.2.3-beta.2", true),
                arguments("1.3.0", "~1.2.3", false),
                arguments("1.2.2", "~1.2.3", false),
                arguments("1.1.0", "~1.2", false),
                arguments("1.3.0", "~1.2", false),
                arguments("2.0.0", "~1", false),
                arguments("0.0.0", "~1", false),
                arguments("1.2.3-beta.1", "~1.2.3-beta.2", false),
                arguments("0.0.7", "~1.9.1-6", false),

                // Caret ranges:
                arguments("16.14.0", "^16.0.0-0", true),
                arguments("1.2.3", "^1.2.3", true),
                arguments("1.2.4", "^1.2.3", true),
                arguments("1.3.0", "^1.2.3", true),
                arguments("0.2.3", "^0.2.3", true),
                arguments("0.2.4", "^0.2.3", true),
                arguments("0.0.3", "^0.0.3", true),
                arguments("0.0.3+exp.sha.5114f85", "^0.0.3", true),
                arguments("0.0.3", "^0.0.3-beta", true),
                arguments("0.0.3-pr.2", "^0.0.3-beta", true),
                arguments("1.2.2", "^1.2.3", false),
                arguments("2.0.0", "^1.2.3", false),
                arguments("0.2.2", "^0.2.3", false),
                arguments("0.3.0", "^0.2.3", false),
                arguments("0.0.4", "^0.0.3", false),
                arguments("0.0.3-alpha", "^0.0.3-beta", false),
                arguments("0.0.4", "^0.0.3-beta", false),

                // Comparators:
                arguments("2.0.0", "=2.0.0", true),
                arguments("2.0.0", "=2.0", true),
                arguments("2.0.1", "=2.0", true),
                arguments("2.0.0", "=2", true),
                arguments("2.0.1", "=2", true),
                arguments("2.0.1", "=2.0.0", false),
                arguments("1.9.9", "=2.0.0", false),
                arguments("1.9.9", "=2.0", false),
                arguments("1.9.9", "=2", false),

                arguments("2.0.1", ">2.0.0", true),
                arguments("3.0.0", ">2.0.0", true),
                arguments("3.0.0", ">2.0", true),
                arguments("3.0.0", ">2", true),
                arguments("2.0.0", ">2.0.0", false),
                arguments("1.9.9", ">2.0.0", false),
                arguments("2.0.0", ">2.0", false),
                arguments("1.9.9", ">2.0", false),
                arguments("2.0.1", ">2", false),
                arguments("2.0.0", ">2", false),
                arguments("1.9.9", ">2", false),

                arguments("1.9.9", "<2.0.0", true),
                arguments("1.9.9", "<2.0", true),
                arguments("1.9.9", "<2", true),
                arguments("2.0.0", "<2.0.0", false),
                arguments("2.0.1", "<2.0.0", false),
                arguments("3.0.0", "<2.0.0", false),
                arguments("2.0.0", "<2.0", false),
                arguments("2.0.1", "<2.0", false),
                arguments("3.0.0", "<2.0", false),
                arguments("2.0.0", "<2", false),
                arguments("2.0.1", "<2", false),
                arguments("3.0.0", "<2", false),

                arguments("2.0.0", ">=2.0.0", true),
                arguments("2.0.1", ">=2.0.0", true),
                arguments("3.0.0", ">=2.0.0", true),
                arguments("2.0.0", ">=2.0", true),
                arguments("3.0.0", ">=2.0", true),
                arguments("2.0.0", ">=2", true),
                arguments("2.0.1", ">=2", true),
                arguments("3.0.0", ">=2", true),
                arguments("1.9.9", ">=2.0.0", false),
                arguments("1.9.9", ">=2.0", false),
                arguments("1.9.9", ">=2", false),
                arguments("3.3.1-alpha", ">=2.4.x", false),

                arguments("3.3.1", ">=2.4.x", true),
                arguments("1.9.9", "<=2.0.0", true),
                arguments("2.0.0", "<=2.0.0", true),
                arguments("1.9.9", "<=2.0", true),
                arguments("2.0.0", "<=2.0", true),
                arguments("2.0.1", "<=2.0", true),
                arguments("2.1.0", "<=2.0", false),
                arguments("1.9.9", "<=2", true),
                arguments("2.0.0", "<=2", true),
                arguments("2.0.1", "<=2", true),
                arguments("2.2.0", "<=2", true),
                arguments("2.0.1", "<=2.0.0", false),
                arguments("3.0.0", "<=2.0.0", false),
                arguments("3.0.0", "<=2.0", false),
                arguments("3.0.0", "<=2", false),

                // AND ranges:
                arguments("2.0.1", ">2.0.0 <3.0.0", true),
                arguments("2.0.1", ">2.0 <3.0", false),

                arguments("1.2.0", "1.2 <1.2.8", true),
                arguments("1.2.7", "1.2 <1.2.8", true),
                arguments("1.1.9", "1.2 <1.2.8", false),
                arguments("1.2.9", "1.2 <1.2.8", false),

                // OR ranges:
                arguments("1.2.3", "1.2.3 || 1.2.4", true),
                arguments("1.2.4", "1.2.3 || 1.2.4", true),
                arguments("1.2.5", "1.2.3 || 1.2.4", false),

                // Complex ranges:
                arguments("1.2.2", ">1.2.1 <1.2.8 || >2.0.0", true),
                arguments("1.2.7", ">1.2.1 <1.2.8 || >2.0.0", true),
                arguments("2.0.1", ">1.2.1 <1.2.8 || >2.0.0", true),
                arguments("1.2.1", ">1.2.1 <1.2.8 || >2.0.0", false),
                arguments("2.0.0", ">1.2.1 <1.2.8 || >2.0.0", false),

                arguments("1.2.2", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("1.2.7", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("2.0.1", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("2.5.0", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", true),
                arguments("1.2.1", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),
                arguments("1.2.8", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),
                arguments("2.0.0", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),
                arguments("3.0.0", ">1.2.1 <1.2.8 || >2.0.0 <3.0.0", false),

                arguments("1.2.2", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("1.2.7", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("2.0.1", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("2.5.0", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", true),
                arguments("1.2.1", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),
                arguments("1.2.8", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),
                arguments("2.0.0", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),
                arguments("3.0.0", "1.2.2 - 1.2.7 || 2.0.1 - 2.9.9", false),

                arguments("1.2.0", "1.2 <1.2.8 || >2.0.0", true),
                arguments("1.2.7", "1.2 <1.2.8 || >2.0.0", true),
                arguments("1.2.7", "1.2 <1.2.8 || >2.0.0", true),
                arguments("2.0.1", "1.2 <1.2.8 || >2.0.0", true),
                arguments("1.1.0", "1.2 <1.2.8 || >2.0.0", false),
                arguments("1.2.9", "1.2 <1.2.8 || >2.0.0", false),
                arguments("2.0.0", "1.2 <1.2.8 || >2.0.0", false),
                arguments("2.6.9", "<= 2.6.8 || >= 3.0.0 <= 3.0.1", false),

                arguments("1.2.2", " ~> 1.2.3 ", false),
                arguments("1.2.3", " ~> 1.2.3 ", true),
                arguments("1.2.4", " ~> 1.2.3 ", true),
                arguments("1.3.0", " ~> 1.2.3 ", false),
                arguments("2.2.0", " ~> 2.2 ", true),
                arguments("2.3.0", " ~> 2.2 ", false),

                arguments("0.0.9", "[1.0,2.0]", false),
                arguments("1.0.0", "[1.0,2.0]", true),
                arguments("2.0.0", "[1.0,2.0]", true),
                arguments("1.5.6", "[1.0,2.0]", true),
                arguments("2.0.1", "[1.0,2.0]", false),

                arguments("2.0.0", "[1.0,2.0[", false),
                arguments("1.0.0", "[1.0,2.0[", true),
                arguments("0.0.9", "[1.0,2.0[", false),
                arguments("2.0.1", "[1.0,2.0[", false),
                arguments("1.5.6", "[1.0,2.0[", true),

                arguments("1.0.0", "]1.0,2.0]", false),
                arguments("1.5.6", "]1.0,2.0]", true),
                arguments("2.0.0", "]1.0,2.0]", true),
                arguments("2.0.1", "]1.0,2.0]", false),

                arguments("1.0.0", "]1.0,2.0[", false),
                arguments("2.0.0", "]1.0,2.0[", false),
                arguments("1.5.6", "]1.0,2.0[", true),

                arguments("1.0.0", "[1.0,)", true),
                arguments("1.0.100", "[1.0,)", true),
                arguments("100.0.1", "[1.0,)", true),
                arguments("0.0.9", "[1.0,)", false),

                arguments("1.0.0", "]1.0,)", false),
                arguments("1.0.100", "]1.0,)", true),
                arguments("100.0.1", "]1.0,)", true),
                arguments("0.0.9", "]1.0,)", false),

                arguments("2.0.0", "(,2.0]", true),
                arguments("2.0.10", "(,2.0]", false),
                arguments("3.0.10", "(,2.0]", false),
                arguments("1.0.100", "(,2.0]", true),
                arguments("0.0.9", "(,2.0]", true),

                arguments("2.0.0", "(,2.0[", false),
                arguments("2.0.10", "(,2.0[", false),
                arguments("3.0.10", "(,2.0[", false),
                arguments("1.0.100", "(,2.0[", true),
                arguments("0.0.9", "(,2.0[", true),

                arguments("1.2.0", "1.2.+", true),
                arguments("1.1.90", "1.2.+", false),
                arguments("1.3.0", "1.2.+", false),
                arguments("1.2.90", "1.2.+", true),

                arguments("1.0.0", "1.+", true),
                arguments("2.0.0", "1.+", false),
                arguments("2.0.1", "1.+", false),
                arguments("1.3.0", "1.+", true),
                arguments("1.2.90", "1.+", true),

                arguments("0.0.0", "latest.integration", true)
        );
    }

    @Test
    void shouldReturnTrueIfVersionIsValid() {
        //given
        String version = "1.2.3-alpha.1+sha.1234";

        //when
        boolean valid = isValid(version);

        //then
        assertThat(valid).isTrue();
    }

    @Test
    void shouldReturnFalseIfVersionIsValid() {
        //given
        String version = "1.2alpha.1+sha.1234";

        //when
        boolean valid = isValid(version);

        //then
        assertThat(valid).isFalse();
    }

    @Test
    void shouldSatisfiesVersionUsingExpression() {
        //given
        Semver semver = new Semver("1.1.1");

        RangesExpression expression = RangesExpression.less("1.0.0")
                .or(RangesExpression.greater("10.0.1").or(RangesExpression.equal("1.1.1")));

        //when
        boolean satisfies = semver.satisfies(expression);

        //then
        assertThat(satisfies).isTrue();
    }

    private static String repeat(String s, int n) {
        return join("", nCopies(n, s));
    }

    @Test
    void shouldBuildDefaultSemverUsingBuilder() {
        //given
        Semver.Builder builder = new Semver.Builder();

        //when
        Semver semver = builder.toSemver();

        //then
        assertThat(semver.getVersion()).isEqualTo("0.0.0");
    }
}
