package org.semver4j.internal.range.processor;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CaretProcessorTest {
    private final CaretProcessor processor = new CaretProcessor();

    @Test
    void shouldParseRangeWhenMinorIsX() {
        //when
        String range = processor.process("^1");

        //then
        assertThat(range).isEqualTo(">=1.0.0 <2.0.0");
    }

    @Test
    void shouldParseRangeWhenPatchIsX() {
        //when
        String range = processor.process("^1.1");

        //then
        assertThat(range).isEqualTo(">=1.1.0 <2.0.0");
    }

    @Test
    void shouldParseRangeWhenPatchIsXAndMajorIsZero() {
        //when
        String range = processor.process("^0.1");

        //then
        assertThat(range).isEqualTo(">=0.1.0 <0.2.0");
    }

    @Test
    void shouldParseRangeWhenPreReleaseIsSetMajorIsZero() {
        //when
        String range = processor.process("^0.1.0-alpha.1");

        //then
        assertThat(range).isEqualTo(">=0.1.0-alpha.1 <0.2.0");
    }
}
