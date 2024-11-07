package org.semver4j.internal.range.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class HyphenProcessorTest {
    private final HyphenProcessor hyphenProcessor = new HyphenProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldParseHyphenRange(String range, String expectedString) {
        assertThat(hyphenProcessor.tryProcess(range)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseHyphenRange() {
        return Stream.of(
                arguments("1.2.3 - 2.3.4", ">=1.2.3 <2.3.5"),
                arguments("1.2 - 2.3.4", ">=1.2.0 <2.3.5"),
                arguments("1 - 2.3.4", ">=1.0.0 <2.3.5"),
                arguments("1.2.3 - 2.3", ">=1.2.3 <2.4.0"),
                arguments("1.2.3 - 2", ">=1.2.3 <3.0.0"),
                arguments("1.2.3-alpha - 2.1.4-beta", ">=1.2.3-alpha <=2.1.4-beta"),
                arguments("1.2 - 2.1.4-beta", ">=1.2.0 <=2.1.4-beta"),
                arguments("1.2.3-alpha - 2.1.4", ">=1.2.3-alpha <2.1.5"),
                arguments("INVALID", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldParseHyphenRangeIncludePrerelease(String range, String expectedString) {
        assertThat(hyphenProcessor.includePrerelease().tryProcess(range)).isEqualTo(expectedString);
    }

    static Stream<Arguments> shouldParseHyphenRangeIncludePrerelease() {
        return Stream.of(
                arguments("1.2.3 - 2.3.4", ">=1.2.3 <2.3.5-0"),
                arguments("1.2 - 2.3.4", ">=1.2.0 <2.3.5-0"),
                arguments("1 - 2.3.4", ">=1.0.0 <2.3.5-0"),
                arguments("1.2.3 - 2.3", ">=1.2.3 <2.4.0-0"),
                arguments("1.2.3 - 2", ">=1.2.3 <3.0.0-0"),
                arguments("1.2.3-alpha - 2.1.4-beta", ">=1.2.3-alpha <=2.1.4-beta"),
                arguments("1.2 - 2.1.4-beta", ">=1.2.0 <=2.1.4-beta"),
                arguments("1.2.3-alpha - 2.1.4", ">=1.2.3-alpha <2.1.5-0"),
                arguments("INVALID", null)
        );
    }
}
