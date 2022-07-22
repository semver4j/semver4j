package org.semver4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.semver4j.Semver.VersionDiff;

import java.util.List;
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

class SemverTest {
    @ParameterizedTest
    @ValueSource(strings = {"1.Y.3", "1.2.Y", "1.1.1.1", "1.0.0+", "1.0.0-", "1.0.0-alpha..1", "1.0.0-001", "1.0.0-äöü", "1.2.3."})
    void shouldThrowExceptionWhenSemverIsNotValid(String version) {
        //when/then
        assertThatThrownBy(() -> new Semver(version))
                .isInstanceOf(SemverException.class)
                .hasMessage(format("Version [%s] is not valid semver.", version));
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
                arguments("1.0.0-alpha.12.ab-c", "1.0.0-alpha.12.ab-c", false)
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
                arguments("1.2.3-alpha.4+sha32iddfu987", SUFFIX),
                arguments("1.2.3-beta.5+sha32iddfu987", SUFFIX),
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

    public static Stream<Arguments> coerceVersions() {
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
                arguments(format("%s.2", repeat("1", 17)), "2.0.0"),
                arguments(format("%s.2.3", repeat("1", 17)), "2.3.0"),
                arguments(format("1.%s.3", repeat("2", 17)), "1.0.0"),
                arguments(format("1.2.%s", repeat("3", 17)), "1.2.0"),
                arguments(format("%s.2.3.4", repeat("1", 17)), "2.3.4"),
                arguments(format("1.%s.3.4", repeat("2", 17)), "1.0.0"),
                arguments(format("1.2.%s.4", repeat("3", 17)), "1.2.0"),
                arguments("10", "10.0.0")
        );
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

    private static String repeat(String s, int n) {
        return join("", nCopies(n, s));
    }

    //    @Test
//    public void statisfies_calls_the_requirement() {
//        Requirement req = mock(Requirement.class);
//        Semver semver = new Semver("1.2.2");
//        semver.satisfies(req);
//        verify(req).isSatisfiedBy(semver);
//    }
//
//
//    @Test
//    public void withIncMinor_test() {
//        Semver semver = new Semver("1.2.3-Beta.4+SHA123456789");
//        semver.withIncMinor(2).isEqualTo("1.4.3-Beta.4+SHA123456789");
//    }
//
//    @Test
//    public void withIncPatch_test() {
//        Semver semver = new Semver("1.2.3-Beta.4+SHA123456789");
//        semver.withIncPatch(2).isEqualTo("1.2.5-Beta.4+SHA123456789");
//    }
//
//    @Test
//    public void withClearedSuffix_test() {
//        Semver semver = new Semver("1.2.3-Beta.4+SHA123456789");
//        semver.withClearedSuffix().isEqualTo("1.2.3+SHA123456789");
//    }
//
//    @Test
//    public void withClearedBuild_test() {
//        Semver semver = new Semver("1.2.3-Beta.4+sha123456789");
//        semver.withClearedBuild().isEqualTo("1.2.3-Beta.4");
//    }
//
//    @Test
//    public void withClearedBuild_test_multiple_hyphen_signs() {
//        Semver semver = new Semver("1.2.3-Beta.4-test+sha12345-6789");
//        semver.withClearedBuild().isEqualTo("1.2.3-Beta.4-test");
//    }
//
//    @Test
//    public void withClearedSuffixAndBuild_test() {
//        Semver semver = new Semver("1.2.3-Beta.4+SHA123456789");
//        semver.withClearedSuffixAndBuild().isEqualTo("1.2.3");
//    }
//
//    @Test
//    public void withSuffix_test_change_suffix() {
//        Semver semver = new Semver("1.2.3-Alpha.4+SHA123456789");
//        Semver result = semver.withSuffix("Beta.1");
//
//        assertEquals("1.2.3-Beta.1+SHA123456789", result.toString());
////        assertArrayEquals(new String[]{"Beta", "1"}, result.getPreRelease());
//    }
//
//    @Test
//    public void withSuffix_test_add_suffix() {
//        Semver semver = new Semver("1.2.3+SHA123456789");
//        Semver result = semver.withSuffix("Beta.1");
//
//        assertEquals("1.2.3-Beta.1+SHA123456789", result.toString());
////        assertArrayEquals(new String[]{"Beta", "1"}, result.getPreRelease());
//    }
//
//    @Test
//    public void withBuild_test_change_build() {
//        Semver semver = new Semver("1.2.3-Alpha.4+SHA123456789");
//        Semver result = semver.withBuild("SHA987654321");
//
//        assertEquals("1.2.3-Alpha.4+SHA987654321", result.toString());
//        assertEquals("SHA987654321", result.getBuild());
//    }
//
//    @Test
//    public void withBuild_test_add_build() {
//        Semver semver = new Semver("1.2.3-Alpha.4");
//        Semver result = semver.withBuild("SHA987654321");
//
//        assertEquals("1.2.3-Alpha.4+SHA987654321", result.toString());
//        assertEquals("SHA987654321", result.getBuild());
//    }
//
//    @Test
//    public void nextMajor_test() {
//        Semver semver = new Semver("1.2.3-beta.4+sha123456789");
//        semver.nextMajor().isEqualTo("2.0.0");
//    }
//
//    @Test
//    public void nextMinor_test() {
//        Semver semver = new Semver("1.2.3-beta.4+sha123456789");
//        semver.nextMinor().isEqualTo("1.3.0");
//    }
//
//    @Test
//    public void nextPatch_test() {
//        Semver semver = new Semver("1.2.3-beta.4+sha123456789");
//        semver.nextPatch().isEqualTo("1.2.4");
//    }
//
//    @Test
//    public void toStrict_test() {
//        String[][] versionGroups = new String[][]{new String[]{"3.0.0-beta.4+sha123456789", "3.0-beta.4+sha123456789", "3-beta.4+sha123456789"}, new String[]{"3.0.0+sha123456789", "3.0+sha123456789", "3+sha123456789"}, new String[]{"3.0.0-beta.4", "3.0-beta.4", "3-beta.4"}, new String[]{"3.0.0", "3.0", "3"},};
//
//        Semver.SemverType[] types = new Semver.SemverType[]{Semver.SemverType.NPM, Semver.SemverType.IVY, Semver.SemverType.LOOSE, Semver.SemverType.COCOAPODS,};
//
//        for (String[] versions : versionGroups) {
//            Semver strict = new Semver(versions[0]);
//            assertEquals(strict, strict.toStrict());
//            for (Semver.SemverType type : types) {
//                for (String version : versions) {
//                    Semver sem = new Semver(version, type);
//                    assertEquals(strict, sem.toStrict());
//                }
//            }
//        }
//    }
//

//
//
//    @Test
//    public void compareTo_without_path_or_minor() {
//        assertTrue(new Semver("1.2.3", Semver.SemverType.LOOSE).isGreaterThan("1.2"));
//        assertTrue(new Semver("1.3", Semver.SemverType.LOOSE).isGreaterThan("1.2.3"));
//        assertTrue(new Semver("1.2.3", Semver.SemverType.LOOSE).isGreaterThan("1"));
//        assertTrue(new Semver("2", Semver.SemverType.LOOSE).isGreaterThan("1.2.3"));
//    }
//
//    @Test
//    public void getValue_returns_the_original_value_trimmed_and_with_the_same_case() {
//        String version = "  1.2.3-BETA.11+sHa.0nSFGKjkjsdf  ";
//        Semver semver = new Semver(version);
//        assertEquals("1.2.3-BETA.11+sHa.0nSFGKjkjsdf", semver.getValue());
//    }
//
//    @Test
//    public void compareTo_with_buildNumber() {
//        Semver v3 = new Semver("1.24.1-rc3+903423.234");
//        Semver v4 = new Semver("1.24.1-rc3+903423.235");
//        assertEquals(0, v3.compareTo(v4));
//    }
}
