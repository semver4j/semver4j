package org.semver4j.internal;

import org.jspecify.annotations.NullMarked;
import org.semver4j.processor.Processor;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public class RangeProcessorPipeline {
    private final List<Processor> processors = new ArrayList<>();

    public RangeProcessorPipeline(final Processor currentProcessor) {
        this.processors.add(currentProcessor);
    }

    public RangeProcessorPipeline addProcessor(final Processor processor) {
        processors.add(processor);
        return this;
    }

    public String process(final String range, boolean includePrerelease) {
        for (Processor processor : processors) {
            String processedRange = processor.process(range, includePrerelease);
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
