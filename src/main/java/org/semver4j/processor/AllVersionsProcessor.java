package org.semver4j.processor;

import static org.semver4j.internal.Utils.ALL_RANGE;
import static org.semver4j.internal.Utils.ALL_RANGE_WITH_PRERELEASE;

import org.jspecify.annotations.Nullable;
import org.semver4j.internal.Utils;

/**
 * Processor for translating wildcard ({@code *}) and empty strings into a classic version range.
 *
 * <p>This processor handles the most basic range expressions that match any valid version:
 *
 * <ul>
 *   <li>{@code *} to {@code ≥0.0.0}
 *   <li>An empty string to {@code ≥0.0.0}
 * </ul>
 *
 * <p>If the {@code includePreRelease} flag is set to {@code true}, the processor will translate:
 *
 * <ul>
 *   <li>{@code *} to {@code ≥0.0.0-0}
 *   <li>An empty string to {@code ≥0.0.0-0}
 * </ul>
 *
 * @see Processor
 * @see Utils#ALL_RANGE
 * @see Utils#ALL_RANGE_WITH_PRERELEASE
 */
public class AllVersionsProcessor implements Processor {
    /**
     * Processes wildcard and empty string ranges into the standard version range format.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include {@code pre-release} versions in the range
     * @return the processed range string if the input is a wildcard or empty string, or {@code null} if this processor
     *     cannot handle the input
     */
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        if (range.equals("*") || range.isEmpty()) {
            return includePreRelease ? ALL_RANGE_WITH_PRERELEASE : ALL_RANGE;
        }
        return null;
    }
}
