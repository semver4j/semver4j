package org.semver4j.internal.range.processor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * <p>Processor for translating {@code *} and empty strings into a classic range.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>{@code *} to {@code ≥0.0.0}</li>
 *     <li>An empty string to {@code ≥0.0.0}</li>
 * </ul>
 *
 * If the prerelease flag is set to true, will translate:
 * <ul>
 *     <li>{@code *} to {@code ≥0.0.0-0}</li>
 *     <li>An empty string to {@code ≥0.0.0-0}</li>
 * </ul>
 */
@NullMarked
public class AllVersionsProcessor extends Processor {
    @Override
    @Nullable
    public String tryProcess(String range) {
        if (range.equals("*") || range.isEmpty()) {
            return this.getIncludePrerelease() ? RangesUtils.ALL_RANGE_WITH_PRERELEASE : RangesUtils.ALL_RANGE;
        }
        return null;
    }
}
