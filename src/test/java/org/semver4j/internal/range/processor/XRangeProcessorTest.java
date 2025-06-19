package org.semver4j.internal.range.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class XRangeProcessorTest {
    private final XRangeProcessor xRangeProcessor = new XRangeProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldParseXRange(String range, String expectedString) {
        assertThat(xRangeProcessor.tryProcess(range)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseXRange() {
        return Stream.of(
                arguments(">1.X.X", ">=2.0.0"),
                arguments(">1.2.X", ">=1.3.0"),
                arguments("<=1.X.X", "<2.0.0"),
                arguments("<=1.2.X", "<1.3.0"),
                arguments(">=1.2.X", ">=1.2.0"),
                arguments(">=1.X.X", ">=1.0.0"),
                arguments("<1.X.X", "<1.0.0"),
                arguments("<1.2.X", "<1.2.0"),
                arguments("1.X", ">=1.0.0 <2.0.0"),
                arguments("1.2.X", ">=1.2.0 <1.3.0"),
                arguments("=1.2.X", ">=1.2.0 <1.3.0"),
                arguments(">=1.2.3 <2.0.0", ">=1.2.3 <2.0.0"),
                arguments("INVALID", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldParseXRangeIncludePrerelease(String range, String expectedString) {
        assertThat(xRangeProcessor.includePrerelease().tryProcess(range)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseXRangeIncludePrerelease() {
        return Stream.of(
                arguments(">1.X.X", ">=2.0.0-0"),
                arguments(">1.2.X", ">=1.3.0-0"),
                arguments("<=1.X.X", "<2.0.0-0"),
                arguments("<=1.2.X", "<1.3.0-0"),
                arguments(">=1.2.X", ">=1.2.0-0"),
                arguments(">=1.X.X", ">=1.0.0-0"),
                arguments("<1.X.X", "<1.0.0-0"),
                arguments("<1.2.X", "<1.2.0-0"),
                arguments("1.X", ">=1.0.0-0 <2.0.0-0"),
                arguments("1.2.X", ">=1.2.0-0 <1.3.0-0"),
                arguments("=1.2.X", ">=1.2.0-0 <1.3.0-0"),
                arguments(">=1.2.3 <2.0.0", ">=1.2.3 <2.0.0"),
                arguments("INVALID", null)
        );
    }
}
