package org.semver4j.processor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.semver4j.internal.Utils;

/**
 * Processor for translating {@code *} and empty strings into a classic range. <br>
 * Translates:
 *
 * <ul>
 *   <li>{@code *} to {@code ≥0.0.0}
 *   <li>An empty string to {@code ≥0.0.0}
 * </ul>
 *
 * <p>If the prerelease flag is set to true, will translate:
 *
 * <ul>
 *   <li>{@code *} to {@code ≥0.0.0-0}
 *   <li>An empty string to {@code ≥0.0.0-0}
 * </ul>
 */
@NullMarked
public class AllVersionsProcessor implements Processor {
    @Override
    @Nullable
    public String process(String range, boolean includePreRelease) {
        if (range.equals("*") || range.isEmpty()) {
            return includePreRelease ? Utils.ALL_RANGE_WITH_PRERELEASE : Utils.ALL_RANGE;
        }
        return null;
    }
}
