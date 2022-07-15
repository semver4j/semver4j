package org.semver4j.parsers;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.semver4j.SemverException;

import java.util.stream.Stream;

import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StrictSemverParserTest {
    @ParameterizedTest
    @MethodSource("validStrictSemver")
    void shouldParseValidVersions(String version, ParsedVersion expected) {
        //given
        StrictSemverParser strictSemverParser = new StrictSemverParser();

        //when
        ParsedVersion actual = strictSemverParser.parse(version);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> validStrictSemver() {
        return Stream.of(
                arguments("0.0.4", new ParsedVersion(0, 0, 4)),
                arguments("1.2.3", new ParsedVersion(1, 2, 3)),
                arguments("10.20.30", new ParsedVersion(10, 20, 30)),
                arguments("1.1.2-prerelease+meta", new ParsedVersion(1, 1, 2, "prerelease", "meta")),
                arguments("1.1.2+meta", new ParsedVersion(1, 1, 2, null, "meta")),
                arguments("1.1.2+meta-valid", new ParsedVersion(1, 1, 2, null, "meta-valid")),
                arguments("1.0.0-alpha", new ParsedVersion(1, 0, 0, "alpha", null)),
                arguments("1.0.0-beta", new ParsedVersion(1, 0, 0, "beta", null)),
                arguments("1.0.0-alpha.beta", new ParsedVersion(1, 0, 0, "alpha.beta", null)),
                arguments("1.0.0-alpha.beta.1", new ParsedVersion(1, 0, 0, "alpha.beta.1", null)),
                arguments("1.0.0-alpha.1", new ParsedVersion(1, 0, 0, "alpha.1", null)),
                arguments("1.0.0-alpha0.valid", new ParsedVersion(1, 0, 0, "alpha0.valid", null)),
                arguments("1.0.0-alpha.0valid", new ParsedVersion(1, 0, 0, "alpha.0valid", null)),
                arguments("1.0.0-alpha-a.b-c-somethinglong+build.1-aef.1-its-okay", new ParsedVersion(1, 0, 0, "alpha-a.b-c-somethinglong", "build.1-aef.1-its-okay")),
                arguments("1.0.0-rc.1+build.1", new ParsedVersion(1, 0, 0, "rc.1", "build.1")),
                arguments("2.0.0-rc.1+build.123", new ParsedVersion(2, 0, 0, "rc.1", "build.123")),
                arguments("1.2.3-beta", new ParsedVersion(1, 2, 3, "beta", null)),
                arguments("10.2.3-DEV-SNAPSHOT", new ParsedVersion(10, 2, 3, "DEV-SNAPSHOT", null)),
                arguments("1.2.3-SNAPSHOT-123", new ParsedVersion(1, 2, 3, "SNAPSHOT-123", null)),
                arguments("1.0.0", new ParsedVersion(1, 0, 0)),
                arguments("2.0.0", new ParsedVersion(2, 0, 0)),
                arguments("1.1.7", new ParsedVersion(1, 1, 7)),
                arguments("2.0.0+build.1848", new ParsedVersion(2, 0, 0, null, "build.1848")),
                arguments("2.0.1-alpha.1227", new ParsedVersion(2, 0, 1, "alpha.1227", null)),
                arguments("1.0.0-alpha+beta", new ParsedVersion(1, 0, 0, "alpha", "beta")),
                arguments("1.2.3----RC-SNAPSHOT.12.9.1--.12+788", new ParsedVersion(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", "788")),
                arguments("1.2.3----R-S.12.9.1--.12+meta", new ParsedVersion(1, 2, 3, "---R-S.12.9.1--.12", "meta")),
                arguments("1.2.3----RC-SNAPSHOT.12.9.1--.12", new ParsedVersion(1, 2, 3, "---RC-SNAPSHOT.12.9.1--.12", null)),
                arguments("1.0.0+0.build.1-rc.10000aaa-kk-0.1", new ParsedVersion(1, 0, 0, null, "0.build.1-rc.10000aaa-kk-0.1")),
//todo need to be handled
//                arguments("99999999999999999999999.999999999999999999.99999999999999999", new ParsedVersion(99999999999999999999999, 999999999999999999, 99999999999999999)),
                arguments("1.0.0-0A.is.legal", new ParsedVersion(1, 0, 0, "0A.is.legal", null))
        );
    }

    @ParameterizedTest
    @MethodSource("invalidStrictSemver")
    void shouldParseInvalidVersions(String version) {
        //given
        StrictSemverParser strictSemverParser = new StrictSemverParser();

        //when/then
        assertThatThrownBy(() -> strictSemverParser.parse(version))
                .isInstanceOf(SemverException.class)
                .hasMessage(format("Version [%s] is not valid to strict semver.", version));
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
                arguments("99999999999999999999999.999999999999999999.99999999999999999----RC-SNAPSHOT.12.09.1--------------------------------..12"),
                arguments("1.1.1.1")
        );
    }
}
