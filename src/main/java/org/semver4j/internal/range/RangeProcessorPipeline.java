package org.semver4j.internal.range;

import org.semver4j.internal.range.processor.Processor;

public class RangeProcessorPipeline {
    private final Processor currentProcessor;

    public RangeProcessorPipeline(Processor currentProcessor) {
        this.currentProcessor = currentProcessor;
    }

    public RangeProcessorPipeline addProcessor(Processor processor) {
        return new RangeProcessorPipeline(version -> processor.process(currentProcessor.process(version)));
    }

    public String process(String range) {
        return currentProcessor.process(range);
    }

    public static RangeProcessorPipeline startWith(Processor processor) {
        return new RangeProcessorPipeline(processor);
    }
}
