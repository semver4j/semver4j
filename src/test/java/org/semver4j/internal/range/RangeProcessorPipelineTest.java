package org.semver4j.internal.range;

import org.junit.jupiter.api.Test;
import org.semver4j.internal.range.processor.Processor;

import static org.assertj.core.api.Assertions.assertThat;

class RangeProcessorPipelineTest {
    private final Processor processorA = range -> range + "_A";
    private final Processor processorB = range -> range + "_B";
    private final Processor processorC = range -> range + "_C";

    @Test
    void shouldBeAbleToBuildAPipeline() {
        RangeProcessorPipeline pipeline = RangeProcessorPipeline.startWith(processorA)
                .addProcessor(processorB)
                .addProcessor(processorC);
        assertThat(pipeline.process("RANGE")).isEqualTo("RANGE_A_B_C");
    }
}
