package org.semver4j.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.semver4j.Semver.VersionDiff.*;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.semver4j.Semver;
import org.semver4j.Semver.VersionDiff;

class DifferTest {
    @ParameterizedTest
    @MethodSource("versions")
    void shouldName(Semver v1, Semver v2, VersionDiff expected) {
        // when
        VersionDiff versionDiff = Differ.diff(v1, v2);

        // then
        assertThat(versionDiff).isEqualTo(expected);
    }

    public static Stream<Arguments> versions() {
        return Stream.of(
                arguments(new Semver("0.1.2"), new Semver("1.1.2"), MAJOR),
                arguments(new Semver("1.2.3"), new Semver("1.3.3"), MINOR),
                arguments(new Semver("1.2.3"), new Semver("1.2.4"), PATCH),
                arguments(new Semver("1.2.3-rc.1"), new Semver("1.2.3-rc.2"), PRE_RELEASE),
                arguments(new Semver("1.2.3-rc.1+c817459c"), new Semver("1.2.3-rc.1+81134d17"), BUILD),
                arguments(new Semver("1.2.3-rc.1+c817459c"), new Semver("1.2.3-rc.1+c817459c"), NONE));
    }
}
