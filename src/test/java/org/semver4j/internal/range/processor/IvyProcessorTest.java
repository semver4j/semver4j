package org.semver4j.internal.range.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IvyProcessorTest {
    private final IvyProcessor ivyProcessor = new IvyProcessor();

    @ParameterizedTest
    @MethodSource
    void shouldProcessIvyRanges(String range, String expected) {
        assertThat(ivyProcessor.tryProcess(range)).isEqualTo(expected);
    }

    static Stream<Arguments> shouldProcessIvyRanges() {
        return Stream.of(
                arguments("[1.0,2.0]", ">=1.0.0 <=2.0.0"),
                arguments("[1.0,2.0[", ">=1.0.0 <2.0.0"),
                arguments("]1.0,2.0]", ">1.0.0 <=2.0.0"),
                arguments("]1.0,2.0[", ">1.0.0 <2.0.0"),
                arguments("[1.0,)", ">=1.0.0"),
                arguments("]1.0,)", ">1.0.0"),
                arguments("(,2.0]", "<=2.0.0"),
                arguments("(,2.0[", "<2.0.0"),
                arguments("[1.0.1,2.0.1]", ">=1.0.1 <=2.0.1"),
                arguments("]1.0.1,2.0.1]", ">1.0.1 <=2.0.1"),
                arguments("[1.0.1,2.0.1[", ">=1.0.1 <2.0.1"),
                arguments("]1.0.1,2.0.1[", ">1.0.1 <2.0.1"),
                arguments("[1.0,2.0.1]", ">=1.0.0 <=2.0.1"),
                arguments("[1.0.1,2.0]", ">=1.0.1 <=2.0.0"),
                arguments("latest", ">=0.0.0"),
                arguments("latest.integration", ">=0.0.0"),
                arguments("INVALID", null)
        );
    }

    @ParameterizedTest
    @MethodSource
    void shouldProcessIvyRangesIncludePrerelease(String range, String expected) {
        assertThat(ivyProcessor.includePrerelease().tryProcess(range)).isEqualTo(expected);
    }

    static Stream<Arguments> shouldProcessIvyRangesIncludePrerelease() {
        return Stream.of(
                arguments("[1.0,2.0]", ">=1.0.0 <=2.0.0"),
                arguments("[1.0,2.0[", ">=1.0.0 <2.0.0"),
                arguments("]1.0,2.0]", ">1.0.0 <=2.0.0"),
                arguments("]1.0,2.0[", ">1.0.0 <2.0.0"),
                arguments("[1.0,)", ">=1.0.0"),
                arguments("]1.0,)", ">1.0.0"),
                arguments("(,2.0]", "<=2.0.0"),
                arguments("(,2.0[", "<2.0.0"),
                arguments("[1.0.1,2.0.1]", ">=1.0.1 <=2.0.1"),
                arguments("]1.0.1,2.0.1]", ">1.0.1 <=2.0.1"),
                arguments("[1.0.1,2.0.1[", ">=1.0.1 <2.0.1"),
                arguments("]1.0.1,2.0.1[", ">1.0.1 <2.0.1"),
                arguments("[1.0,2.0.1]", ">=1.0.0 <=2.0.1"),
                arguments("[1.0.1,2.0]", ">=1.0.1 <=2.0.0"),
                arguments("latest", ">=0.0.0-0"),
                arguments("latest.integration", ">=0.0.0-0"),
                arguments("INVALID", null)
        );
    }
}
