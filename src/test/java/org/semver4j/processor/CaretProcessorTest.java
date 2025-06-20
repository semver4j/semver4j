package org.semver4j.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CaretProcessorTest {
    private final CaretProcessor processor = new CaretProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldParseCaretRange(String range, String expectedString) {
        assertThat(processor.process(range, false)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseCaretRange() {
        return Stream.of(
                arguments("^1", ">=1.0.0 <2.0.0"),
                arguments("^1.1", ">=1.1.0 <2.0.0"),
                arguments("^1.1.1", ">=1.1.1 <2.0.0"),
                arguments("^0.1", ">=0.1.0 <0.2.0"),
                arguments("^0.0.1", ">=0.0.1 <0.0.2"),
                arguments("^1.0.0-alpha.1", ">=1.0.0-alpha.1 <2.0.0"),
                arguments("^0.1.1-alpha.1", ">=0.1.1-alpha.1 <0.2.0"),
                arguments("INVALID", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldParseCaretRangeIncludePrerelease(String range, String expectedString) {
        assertThat(processor.process(range, true)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseCaretRangeIncludePrerelease() {
        return Stream.of(
                arguments("^1", ">=1.0.0-0 <2.0.0-0"),
                arguments("^1.1", ">=1.1.0-0 <2.0.0-0"),
                arguments("^1.1.1", ">=1.1.1 <2.0.0-0"),
                arguments("^0.1", ">=0.1.0-0 <0.2.0-0"),
                arguments("^0.0.1", ">=0.0.1 <0.0.2-0"),
                arguments("^1.0.0-alpha.1", ">=1.0.0-alpha.1 <2.0.0-0"),
                arguments("^0.1.1-alpha.1", ">=0.1.1-alpha.1 <0.2.0-0"),
                arguments("INVALID", null)
        );
    }
}
