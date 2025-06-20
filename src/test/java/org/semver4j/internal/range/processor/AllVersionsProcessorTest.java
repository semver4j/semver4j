package org.semver4j.internal.range.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class AllVersionsProcessorTest {
    private final AllVersionsProcessor allVersionsProcessor = new AllVersionsProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldParseAllVersions(String range, String expectedString) {
        assertThat(allVersionsProcessor.process(range, false)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseAllVersions() {
        return Stream.of(
                arguments("*", ">=0.0.0"),
                arguments("", ">=0.0.0"),
                arguments("INVALID", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldParseAllVersionsIncludePrerelease(String range, String expectedString) {
        assertThat(allVersionsProcessor.process(range, true)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseAllVersionsIncludePrerelease() {
        return Stream.of(
                arguments("*", ">=0.0.0-0"),
                arguments("", ">=0.0.0-0"),
                arguments("INVALID", null)
        );
    }
}
