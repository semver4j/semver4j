package org.semver4j.internal;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.semver4j.SemverException;
import org.semver4j.internal.StrictParser.Version;

class StrictParserTest {
    @ParameterizedTest
    @MethodSource("validStrictSemver")
    void shouldParseValidVersions(String version, Version expected) {
        // when
        Version actual = StrictParser.parse(version);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> validStrictSemver() {
        return Stream.of(
                arguments("0.0.4", new Version(0, 0, 4)),
                arguments("1.2.3", new Version(1, 2, 3)),
                arguments("10.20.30", new Version(10, 20, 30)),
                arguments(
                        "1.1.2-prerelease+meta",
                        new Version(1, 1, 2, singletonList("prerelease"), singletonList("meta"))),
                arguments("1.1.2+meta", new Version(1, 1, 2, emptyList(), singletonList("meta"))),
                arguments("1.1.2+meta-valid", new Version(1, 1, 2, emptyList(), singletonList("meta-valid"))),
                arguments("1.0.0-alpha", new Version(1, 0, 0, singletonList("alpha"), emptyList())),
                arguments("1.0.0-beta", new Version(1, 0, 0, singletonList("beta"), emptyList())),
                arguments("1.0.0-alpha.beta", new Version(1, 0, 0, asList("alpha.beta".split("\\.")), emptyList())),
                arguments("1.0.0-alpha.beta.1", new Version(1, 0, 0, asList("alpha.beta.1".split("\\.")), emptyList())),
                arguments("1.0.0-alpha.1", new Version(1, 0, 0, asList("alpha.1".split("\\.")), emptyList())),
                arguments("1.0.0-alpha0.valid", new Version(1, 0, 0, asList("alpha0.valid".split("\\.")), emptyList())),
                arguments("1.0.0-alpha.0valid", new Version(1, 0, 0, asList("alpha.0valid".split("\\.")), emptyList())),
                arguments(
                        "1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay",
                        new Version(
                                1,
                                0,
                                0,
                                asList("alpha-a.b-c-somethinglong".split("\\.")),
                                asList("build.1-aef.1-its-okay".split("\\.")))),
                arguments(
                        "1.0.0-rc.1+build.1",
                        new Version(1, 0, 0, asList("rc.1".split("\\.")), asList("build.1".split("\\.")))),
                arguments(
                        "2.0.0-rc.1+build.123",
                        new Version(2, 0, 0, asList("rc.1".split("\\.")), asList("build.123".split("\\.")))),
                arguments("1.2.3-beta", new Version(1, 2, 3, singletonList("beta"), emptyList())),
                arguments("10.2.3-DEV-SNAPSHOT", new Version(10, 2, 3, singletonList("DEV-SNAPSHOT"), emptyList())),
                arguments("1.2.3-SNAPSHOT-123", new Version(1, 2, 3, singletonList("SNAPSHOT-123"), emptyList())),
                arguments("1.0.0", new Version(1, 0, 0)),
                arguments("2.0.0", new Version(2, 0, 0)),
                arguments("1.1.7", new Version(1, 1, 7)),
                arguments("2.0.0+build.1848", new Version(2, 0, 0, emptyList(), asList("build.1848".split("\\.")))),
                arguments("2.0.1-alpha.1227", new Version(2, 0, 1, asList("alpha.1227".split("\\.")), emptyList())),
                arguments("1.0.0-alpha+beta", new Version(1, 0, 0, singletonList("alpha"), singletonList("beta"))),
                arguments(
                        "1.2.3----RC-SNAPSHOT.12.9.1--.12+788",
                        new Version(1, 2, 3, asList("---RC-SNAPSHOT.12.9.1--.12".split("\\.")), singletonList("788"))),
                arguments(
                        "1.2.3----R-S.12.9.1--.12+meta",
                        new Version(1, 2, 3, asList("---R-S.12.9.1--.12".split("\\.")), singletonList("meta"))),
                arguments(
                        "1.2.3----RC-SNAPSHOT.12.9.1--.12",
                        new Version(1, 2, 3, asList("---RC-SNAPSHOT.12.9.1--.12".split("\\.")), emptyList())),
                arguments(
                        "1.0.0+0.build.1-rc.10000aaa-kk-0.1",
                        new Version(1, 0, 0, emptyList(), asList("0.build.1-rc.10000aaa-kk-0.1".split("\\.")))),
                arguments("1.0.0-0A.is.legal", new Version(1, 0, 0, asList("0A.is.legal".split("\\.")), emptyList())));
    }

    @ParameterizedTest
    @MethodSource("invalidStrictSemver")
    void shouldParseInvalidVersions(String version) {
        // when/then
        assertThatThrownBy(() -> StrictParser.parse(version))
                .isInstanceOf(SemverException.class)
                .hasMessage(format(Locale.ROOT, "Version [%s] is not valid semver.", version));
    }

    @Test
    void shouldParseInvalidVersions() {
        // when/then
        assertThatThrownBy(() -> StrictParser.parse("99999999999999999999999.999999999999999999.99999999999999999"))
                .isInstanceOf(SemverException.class)
                .hasMessage(format(Locale.ROOT, "Value [%s] is too big.", "99999999999999999999999"));
    }

    static Stream<Arguments> invalidStrictSemver() {
        return Stream.of(
                arguments("1"),
                arguments("1.2"),
                arguments("1.2.3-0123"),
                arguments("1.2.3-0123.0123"),
                arguments("1.1.2+.123"),
                arguments("+invalid"),
                arguments("-invalid"),
                arguments("-invalid+invalid"),
                arguments("-invalid .01 "),
                arguments("alpha"),
                arguments("alpha.beta"),
                arguments("alpha.beta.1"),
                arguments("alpha.1"),
                arguments("alpha+beta"),
                arguments("alpha_beta"),
                arguments("alpha."),
                arguments("alpha.."),
                arguments("beta"),
                arguments("1.0.0-alpha_beta"),
                arguments(" - alpha."),
                arguments("1.0.0-alpha.."),
                arguments("1.0.0-alpha..1"),
                arguments("1.0.0-alpha...1"),
                arguments("1.0.0-alpha....1"),
                arguments("1.0.0-alpha.....1"),
                arguments("1.0.0-alpha......1"),
                arguments("1.0.0-alpha.......1"),
                arguments("01.1.1"),
                arguments("1.01.1"),
                arguments("1.1.01"),
                arguments("1.2"),
                arguments("1.2.3.DEV"),
                arguments("1.2-SNAPSHOT"),
                arguments("1.2.31.2.3----RC-SNAPSHOT.12.09.1--..12+788"),
                arguments("1.2-RC-SNAPSHOT"),
                arguments("-1.0.3-gamma+b7718"),
                arguments("+justmeta"),
                arguments("9.8.7+meta+meta"),
                arguments("9.8.7-whatever+meta+meta"),
                arguments(
                        "99999999999999999999999.999999999999999999.99999999999999999----RC-SNAPSHOT.12.09.1--------------------------------..12"),
                arguments("1.1.1.1"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1", "99999999999999999999999.999999999999999999.99999999999999999"})
    void shouldThrowSemverExceptionWhichExtendsIllegalArgumentException(String version) {
        // when/then
        assertThatThrownBy(() -> StrictParser.parse(version)).isInstanceOf(IllegalArgumentException.class);
    }
}
