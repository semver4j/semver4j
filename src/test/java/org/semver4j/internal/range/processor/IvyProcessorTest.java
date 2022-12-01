package org.semver4j.internal.range.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class IvyProcessorTest {
    @ParameterizedTest
    @MethodSource("ivy")
    void shouldProcessIvyRanges(String range, String expected) {
        //given
        IvyProcessor ivyProcessor = new IvyProcessor();

        //when
        String actual = ivyProcessor.process(range);

        //then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> ivy() {
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
            arguments("[1.0.1,2.0]", ">=1.0.1 <=2.0.0")
        );
    }
}
