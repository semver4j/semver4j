package org.semver4j.internal.range.processor;

import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;

public class GreaterThanOrEqualZeroProcessor implements Processor {
    @Override
    public String process(String range) {
        if (range.equals("latest") || range.equals("latest.integration") || range.equals("*") || range.isEmpty()) {
            return format("%s0.0.0", GTE.asString());
        }
        return range;
    }
}
