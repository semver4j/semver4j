package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.semver4j.Semver;

import java.util.Locale;

import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;

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
    public @Nullable String tryProcess(@NotNull String range) {
        if (range.equals("latest") || range.equals("latest.integration") || range.equals("*") || range.isEmpty()) {
            return format(Locale.ROOT, "%s%s", GTE.asString(), Semver.ZERO);
        }
        return range;
    }
}
