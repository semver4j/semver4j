package org.semver4j.internal.range;

import org.jspecify.annotations.NullMarked;
import org.semver4j.internal.range.processor.Processor;

import java.util.ArrayList;

@NullMarked
public class RangeProcessorPipeline {
    private final ArrayList<Processor> processors = new ArrayList<>();

    public RangeProcessorPipeline(final Processor currentProcessor) {
        this.processors.add(currentProcessor);
    }

    public RangeProcessorPipeline addProcessor(final Processor processor) {
        processors.add(processor);
        return this;
    }

    public String process(final String range) {
        for (Processor processor : processors) {
            String processedRange = processor.tryProcess(range);
            if (processedRange != null) {
                return processedRange;
            }
        }
        return range;
    }

    public static RangeProcessorPipeline startWith(final Processor processor) {
        return new RangeProcessorPipeline(processor);
    }
}
