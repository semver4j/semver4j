package org.semver4j.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.semver4j.internal.Modifier.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.semver4j.Semver;

class ModifierTest {
    @ParameterizedTest
    @CsvSource({"1.2.3, 2.0.0", "1.0.0, 2.0.0", "1.2.0, 2.0.0", "1.0.0-alpha, 1.0.0"})
    void nextMajorShouldCreateNewInstanceAndIncrementCorrectly(String input, String expected) {
        // given
        Semver original = new Semver(input);

        // when
        Semver result = nextMajor(original);

        // then
        assertThat(result).isNotSameAs(original).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource({"1.2.3, 2, 3.2.3", "1.2.3, 0, 1.2.3", "1.0.0-alpha, 1, 2.0.0-alpha"})
    void withIncMajorShouldCreateNewInstanceAndIncrementByNumber(String input, int increment, String expected) {
        // given
        Semver original = new Semver(input);

        // when
        Semver result = withIncMajor(original, increment);

        // then
        assertThat(result).isNotSameAs(original).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource({"1.2.3, 1.3.0", "1.2.0, 1.3.0", "1.2.0-alpha, 1.2.0"})
    void nextMinorShouldCreateNewInstanceAndIncrementCorrectly(String input, String expected) {
        // given
        Semver original = new Semver(input);

        // when
        Semver result = nextMinor(original);

        // then
        assertThat(result).isNotSameAs(original).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource({"1.2.3, 2, 1.4.3", "1.2.3, 0, 1.2.3", "1.2.0-alpha, 1, 1.3.0-alpha"})
    void withIncMinorShouldCreateNewInstanceAndIncrementByNumber(String input, int increment, String expected) {
        // given
        Semver original = new Semver(input);

        // when
        Semver result = withIncMinor(original, increment);

        // then
        assertThat(result).isNotSameAs(original).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource({"1.2.3, 1.2.4", "1.2.3-alpha, 1.2.3"})
    void nextPatchShouldCreateNewInstanceAndIncrementCorrectly(String input, String expected) {
        // given
        Semver original = new Semver(input);

        // when
        Semver result = nextPatch(original);

        // then
        assertThat(result).isNotSameAs(original).hasToString(expected);
    }

    @ParameterizedTest
    @CsvSource({"1.2.3, 2, 1.2.5", "1.2.3, 0, 1.2.3", "1.2.3-alpha, 1, 1.2.4-alpha"})
    void withIncPatchShouldCreateNewInstanceAndIncrementByNumber(String input, int increment, String expected) {
        // given
        Semver original = new Semver(input);

        // when
        Semver result = withIncPatch(original, increment);

        // then
        assertThat(result).isNotSameAs(original).hasToString(expected);
    }

    @Test
    void withPreReleaseShouldCreateNewInstanceAndSetPreRelease() {
        // given
        Semver original = new Semver("1.2.3");

        // when
        Semver result = withPreRelease(original, "alpha.1");

        // then
        assertThat(result).isNotSameAs(original).hasToString("1.2.3-alpha.1");
        assertThat(result.getPreRelease()).containsExactly("alpha", "1");
    }

    @Test
    void withBuildShouldCreateNewInstanceAndSetBuild() {
        // given
        Semver original = new Semver("1.2.3");

        // when
        Semver result = withBuild(original, "build.123");

        // then
        assertThat(result).isNotSameAs(original).hasToString("1.2.3+build.123");
        assertThat(result.getBuild()).containsExactly("build", "123");
    }

    @Test
    void withClearedPreReleaseShouldCreateNewInstanceAndClearPreRelease() {
        // given
        Semver original = new Semver("1.2.3-alpha+build");

        // when
        Semver result = withClearedPreRelease(original);

        // then
        assertThat(result).isNotSameAs(original).hasToString("1.2.3+build");
        assertThat(result.getPreRelease()).isEmpty();
    }

    @Test
    void withClearedBuildShouldCreateNewInstanceAndClearBuild() {
        // given
        Semver original = new Semver("1.2.3-alpha+build");

        // when
        Semver result = withClearedBuild(original);

        // then
        assertThat(result).isNotSameAs(original).hasToString("1.2.3-alpha");
        assertThat(result.getBuild()).isEmpty();
    }

    @Test
    void withClearedPreReleaseAndBuildShouldCreateNewInstanceAndClearBoth() {
        // given
        Semver original = new Semver("1.2.3-alpha+build");

        // when
        Semver result = withClearedPreReleaseAndBuild(original);

        // then
        assertThat(result).isNotSameAs(original).hasToString("1.2.3");
        assertThat(result.getPreRelease()).isEmpty();
        assertThat(result.getBuild()).isEmpty();
    }
}
