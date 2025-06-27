package org.semver4j.processor;

import org.jspecify.annotations.Nullable;

/**
 * Interface for processors that translate different version range formats into standard version ranges.
 *
 * <p>The Processor interface is the foundation of the version range processing pipeline in semver4j. Each processor
 * implementation handles a specific version range format (such as caret ranges, hyphen ranges, tilde ranges, etc.) and
 * converts it into a standardized version range representation.
 *
 * <p>Processors are designed to work in a chain of responsibility pattern:
 *
 * <ol>
 *   <li>Each processor attempts to handle the input range string
 *   <li>If a processor can handle the range, it returns the processed result
 *   <li>If a processor cannot handle the range, it returns {@code null} to let the next processor try
 * </ol>
 */
public interface Processor {
    /**
     * Constant representing the lowest possible pre-release version suffix.
     *
     * <p>For example, {@code <2.0.0-0} would match all versions less than 2.0.0, including any pre-release versions
     * like 2.0.0-alpha, 2.0.0-beta, etc.
     */
    String LOWEST_PRE_RELEASE = "-0";

    /**
     * Processes a version range string into a standardized format.
     *
     * <p>Each processor implementation is responsible for recognizing and converting its specific range format. If the
     * processor recognizes the format, it will return the processed range. If not, it will return {@code null} to
     * indicate that another processor should attempt to handle the range.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string if this processor can handle the input, or {@code null} if this processor
     *     cannot handle the input
     */
    @Nullable
    String process(String range, boolean includePreRelease);
}
