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
 * <p>
 * If the prerelease flag is set to true, will translate:
 * <ul>
 *     <li>{@code *} to {@code ≥0.0.0-0}</li>
 *     <li>An empty string to {@code ≥0.0.0-0}</li>
 * </ul>
 */
@NullMarked
public class AllVersionsProcessor implements Processor {
    @Override
    @Nullable
    public String process(String range, boolean includePrerelease) {
        if (range.equals("*") || range.isEmpty()) {
            return includePrerelease ? RangesUtils.ALL_RANGE_WITH_PRERELEASE : RangesUtils.ALL_RANGE;
        }
        return null;
    }
}
