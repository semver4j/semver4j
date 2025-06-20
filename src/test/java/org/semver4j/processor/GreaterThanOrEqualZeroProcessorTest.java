package org.semver4j.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SuppressWarnings("deprecation")
class GreaterThanOrEqualZeroProcessorTest {
    private final GreaterThanOrEqualZeroProcessor processor = new GreaterThanOrEqualZeroProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldParseRanges(String range, String expectedString) {
        assertThat(processor.process(range, false)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseRanges() {
        return Stream.of(
                arguments("latest", ">=0.0.0"),
                arguments("latest.integration", ">=0.0.0"),
                arguments("*", ">=0.0.0"),
                arguments("", ">=0.0.0"),
                arguments("INVALID", null)
        );
    }
}
