package org.semver4j.internal.range.processor;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class GreaterThanOrEqualZeroProcessorTest {
    private final GreaterThanOrEqualZeroProcessor processor = new GreaterThanOrEqualZeroProcessor();

    @ParameterizedTest
    @ValueSource(strings = {"latest", "latest.integration", "*", ""})
    void shouldParseRanges(String range) {
        //when
        String actualRange = processor.process(range);

        //then
        assertThat(actualRange).isEqualTo(">=0.0.0");
    }
}
