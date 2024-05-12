package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.semver4j.Semver;

import java.util.Locale;

import static java.lang.String.format;
import static org.semver4j.Range.RangeOperator.GTE;

/**
 * <p>Processor for translating {@code *} and empty strings into a classic range.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>{@code *} to {@code ≥0.0.0}</li>
 *     <li>an empty string to {@code ≥0.0.0}</li>
 * </ul>
 */
public class AllVersionsProcessor implements Processor {
    @Override
    public @Nullable String tryProcess(@NotNull String range) {
        if (range.equals("*") || range.isEmpty()) {
            return format(Locale.ROOT, "%s%s", GTE.asString(), Semver.ZERO);
        }
        return null;
    }
}
