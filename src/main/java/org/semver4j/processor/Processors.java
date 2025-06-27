package org.semver4j.processor;

import java.util.List;

/**
 * Factory class for creating instances of various version range processors.
 *
 * <p>This class provides a centralized way to access all processor implementations available in the semver4j library.
 *
 * @see Processor
 * @since 6.0.0
 */
public class Processors {
    private Processors() {}

    /**
     * A list of all available processors in recommended processing order.
     *
     * <p>This list contains instances of all processor implementations, ordered in a way that ensures correct
     * precedence when processing version ranges. When processing a version range, each processor should be tried in
     * sequence until one successfully handles the range.
     *
     * <p>The order is important because some range formats may be ambiguous or have overlapping syntax. The processors
     * are ordered to ensure that more specific formats are tried before more general ones.
     */
    public static final List<Processor> ALL_PROCESSORS =
            List.of(allVersions(), ivy(), hyphen(), caret(), tilde(), xRange());

    /**
     * Creates a new instance of {@link AllVersionsProcessor}.
     *
     * <p>The {@link AllVersionsProcessor} handles special version ranges like {@code "*"} or {@codem ""} that match all
     * versions.
     *
     * @return a new AllVersionsProcessor instance
     */
    public static AllVersionsProcessor allVersions() {
        return new AllVersionsProcessor();
    }

    /**
     * Creates a new instance of {@link IvyProcessor}.
     *
     * <p>The {@link IvyProcessor} handles version ranges in Apache Ivy interval notation format, such as
     * {@code "[1.0,2.0]"} or {@code "[1.0,)"} or special keywords like {@code "latest"}.
     *
     * @return a new IvyProcessor instance
     * @see <a href="https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html">Apache Ivy
     *     Version Matchers</a>
     */
    public static IvyProcessor ivy() {
        return new IvyProcessor();
    }

    /**
     * Creates a new instance of {@link HyphenProcessor}.
     *
     * <p>The {@link HyphenProcessor} handles version ranges using hyphen notation, such as {@code "1.2.3 - 2.3.4"}.
     *
     * @return a new HyphenProcessor instance
     * @see <a href="https://github.com/npm/node-semver#hyphen-ranges-xyz---abc">npm SemVer Hyphen Ranges</a>
     */
    public static HyphenProcessor hyphen() {
        return new HyphenProcessor();
    }

    /**
     * Creates a new instance of {@link CaretProcessor}.
     *
     * <p>The {@link CaretProcessor} handles version ranges using caret notation, such as {@code "^1.2.3"}, which allows
     * changes that do not modify the left-most non-zero digit.
     *
     * @return a new CaretProcessor instance
     * @see <a href="https://github.com/npm/node-semver#caret-ranges-123-025-004">npm SemVer Caret Ranges</a>
     */
    public static CaretProcessor caret() {
        return new CaretProcessor();
    }

    /**
     * Creates a new instance of {@link TildeProcessor}.
     *
     * <p>The {@link TildeProcessor} handles version ranges using tilde notation, such as {@code "~1.2.3"}, which allows
     * patch-level changes if a minor version is specified.
     *
     * @return a new TildeProcessor instance
     * @see <a href="https://github.com/npm/node-semver#tilde-ranges-123-12-1">npm SemVer Tilde Ranges</a>
     */
    public static TildeProcessor tilde() {
        return new TildeProcessor();
    }

    /**
     * Creates a new instance of {@link XRangeProcessor}.
     *
     * <p>The {@link XRangeProcessor} handles version ranges using X-Range notation, such as {@code "1.2.x"} or
     * {@code "1.x.x"} or {@code "*"}, which replace the X with a zero when the value is not provided.
     *
     * @return a new XRangeProcessor instance
     * @see <a href="https://github.com/npm/node-semver#x-ranges-12x-1x-12-">npm SemVer X-Ranges</a>
     */
    public static XRangeProcessor xRange() {
        return new XRangeProcessor();
    }
}
