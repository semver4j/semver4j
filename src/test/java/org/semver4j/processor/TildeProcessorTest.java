package org.semver4j.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class TildeProcessorTest {
    private final TildeProcessor tildeProcessor = new TildeProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldParseTildeRange(String range, String expectedString) {
        assertThat(tildeProcessor.process(range, false)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseTildeRange() {
        return Stream.of(
                arguments("~1.2.3", ">=1.2.3 <1.3.0"),
                arguments("~1.2", ">=1.2.0 <1.3.0"),
                arguments("~1", ">=1.0.0 <2.0.0"),
                arguments("~1.2.3-alpha", ">=1.2.3-alpha <1.3.0"),
                arguments("INVALID", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldParseTildeRangeIncludePrerelease(String range, String expectedString) {
        assertThat(tildeProcessor.process(range, true)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseTildeRangeIncludePrerelease() {
        return Stream.of(
                arguments("~1.2.3", ">=1.2.3 <1.3.0-0"),
                arguments("~1.2", ">=1.2.0-0 <1.3.0-0"),
                arguments("~1", ">=1.0.0-0 <2.0.0-0"),
                arguments("~1.2.3-alpha", ">=1.2.3-alpha <1.3.0-0"),
                arguments("INVALID", null)
        );
    }
}
