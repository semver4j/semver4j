package org.semver4j.internal.range.processor;

import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;

import java.util.Locale;

/**
 * <p>Processor for translate {@code latest}, {@code latest.internal} and {@code *} strings into classic range.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>all ranges to {@code ≥0.0.0}</li>
 * </ul>
 */
public class GreaterThanOrEqualZeroProcessor implements Processor {
    @Override
    public String process(String range) {
        if (range.equals("latest") || range.equals("latest.integration") || range.equals("*") || range.isEmpty()) {
            return format(Locale.ROOT, "%s0.0.0", GTE.asString());
        }
        return range;
    }
}
