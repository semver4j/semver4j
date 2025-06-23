package org.semver4j.internal;

import static java.lang.String.format;

import java.util.Locale;

/**
 * Collection of regular expressions used to tokenize and parse <a href="https://semver.org">Semantic Versioning</a>
 * strings.
 */
public class Tokenizers {
    private Tokenizers() {}

    /**
     * Regular expression for non-negative integers without leading zeroes.
     *
     * <p>Valid examples: {@code 0}, {@code 1}, {@code 42}, {@code 123}
     *
     * <p>Invalid examples: {@code 01}, {@code 00}, {@code -1}
     *
     * @see <a href="https://semver.org/#spec-item-2">Semantic Versioning Specification (Item 2)</a>
     */
    private static final String NUMERIC_IDENTIFIER = "0|[1-9]\\d*";

    /**
     * Regular expression for identifiers containing letters, numbers, and hyphens. Must contain at least one
     * non-numeric character.
     *
     * <p>Valid examples: {@code alpha}, {@code beta1}, {@code rc-2}, {@code 20150826}
     *
     * <p>Invalid examples: {@code !invalid}, {@code space included}
     */
    private static final String NON_NUMERIC_IDENTIFIER = "\\d*[a-zA-Z-][a-zA-Z0-9-]*";

    /**
     * Regular expression for the core version components ({@code MAJOR.MINOR.PATCH}).
     *
     * <p>Valid examples: {@code 1.0.0}, {@code 2.3.4}, {@code 0.1.0}
     *
     * <p>Invalid examples: {@code 1}, {@code 1.0}, {@code 01.1.0}
     *
     * @see <a href="https://semver.org/#summary">Semantic Versioning Summary</a>
     */
    private static final String MAIN_VERSION =
            format(Locale.ROOT, "(%s)\\.(%s)\\.(%s)", NUMERIC_IDENTIFIER, NUMERIC_IDENTIFIER, NUMERIC_IDENTIFIER);

    /**
     * Regular expression for a single prerelease identifier.
     *
     * <p>Valid examples: {@code alpha}, {@code beta1}, {@code rc-2}, {@code 123}
     */
    private static final String PRERELEASE_IDENTIFIER =
            format(Locale.ROOT, "(?:%s|%s)", NUMERIC_IDENTIFIER, NON_NUMERIC_IDENTIFIER);

    /**
     * Regular expression for prerelease identifiers - hyphen-prefixed, dot-separated series of identifiers.
     *
     * <p>Valid examples: {@code -alpha}, {@code -beta.1}, {@code -rc.1.2}, {@code -0.3.7}
     *
     * <p>Invalid examples: {@code alpha} (missing hyphen), {@code -beta..1} (empty identifier)
     *
     * @see <a href="https://semver.org/#spec-item-9">Semantic Versioning Specification (Item 9)</a>
     */
    private static final String PRERELEASE =
            format(Locale.ROOT, "(?:-(%s(?:\\.%s)*))", PRERELEASE_IDENTIFIER, PRERELEASE_IDENTIFIER);

    /**
     * Regular expression for a single build metadata identifier.
     *
     * <p>Valid examples: {@code 001}, {@code build}, {@code sha-1}
     *
     * <p>Invalid examples: {@code build.}, {@code sha_1}
     */
    private static final String BUILD_IDENTIFIER = "[0-9A-Za-z-]+";

    /**
     * Regular expression for build metadata - plus-prefixed, dot-separated series of identifiers.
     *
     * <p>Valid examples: {@code +build}, {@code +20130313144700}, {@code +exp.sha.5114f85}
     *
     * <p>Invalid examples: {@code build} (missing plus), {@code +build..2} (empty identifier)
     */
    private static final String BUILD =
            format(Locale.ROOT, "(?:\\+(%s(?:\\.%s)*))", BUILD_IDENTIFIER, BUILD_IDENTIFIER);

    /**
     * Regular expression for a complete, non-anchored SemVer string.
     *
     * <p>Valid examples: {@code 1.0.0}, {@code v1.2.3-alpha+build.1}
     */
    private static final String STRICT_PLAIN = format(Locale.ROOT, "v?%s%s?%s?", MAIN_VERSION, PRERELEASE, BUILD);

    /**
     * Regular expression for a fully valid, anchored SemVer version string.
     *
     * <p>Required components: X.Y.Z ({@code MAJOR.MINOR.PATCH})
     *
     * <p>Optional components: prerelease and build metadata
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code 1.0.0} - Basic version
     *   <li>{@code 1.0.0-alpha} - With prerelease
     *   <li>{@code 1.0.0+build.1} - With build metadata
     *   <li>{@code 1.0.0-beta.2+sha.12345} - With both prerelease and build metadata
     *   <li>{@code v2.0.0} - With 'v' prefix
     * </ul>
     *
     * Invalid examples: {@code 1}, {@code 1.0}, {@code v1.0.0-@invalid}
     */
    public static final String STRICT = format(Locale.ROOT, "^%s$", STRICT_PLAIN);

    /**
     * Regular expression for X-Range identifiers.
     *
     * <p>Includes numeric identifiers and the wildcards {@code x}, {@code X}, {@code *}. Also includes {@code +} for
     * Ivy ranges.
     *
     * <p>Valid examples: {@code 1}, {@code x}, {@code X}, {@code *}, {@code +}
     */
    private static final String XRANGE_IDENTIFIER = format(Locale.ROOT, "%s|x|X|\\*|\\+", NUMERIC_IDENTIFIER);

    /**
     * Regular expression for X-Range expressions.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code 1.x.x} - Any 1.x.x version
     *   <li>{@code 1.2.*} - Any 1.2.x version
     *   <li>{@code 1} - Any version starting with 1
     *   <li>{@code 1.X} - Any version starting with 1
     * </ul>
     */
    private static final String XRANGE_PLAIN = format(
            Locale.ROOT,
            "[v=\\s]*(%s)(?:\\.(%s)(?:\\.(%s)(?:%s)?%s?)?)?",
            XRANGE_IDENTIFIER,
            XRANGE_IDENTIFIER,
            XRANGE_IDENTIFIER,
            PRERELEASE,
            BUILD);

    /** Regular expression for a lone caret ({@code ^}) character. */
    private static final String LONE_CARET = "(?:\\^)";

    /**
     * Regular expression for caret-range expressions.
     *
     * <p>The caret ({@code ^}) allows changes that do not modify the left-most non-zero digit.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code ^1.2.3} - Matches 1.2.3 up to, but not including 2.0.0
     *   <li>{@code ^0.2.3} - Matches 0.2.3 up to, but not including 0.3.0
     *   <li>{@code ^0.0.3} - Matches 0.0.3 up to, but not including 0.0.4
     * </ul>
     */
    public static final String CARET = format(Locale.ROOT, "^%s%s$", LONE_CARET, XRANGE_PLAIN);

    /**
     * Regular expression for hyphen-range expressions.
     *
     * <p>Hyphen ranges specify an inclusive set of versions.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code 1.2.3 - 2.3.4} - Matches all versions from 1.2.3 up to and including 2.3.4
     *   <li>{@code 1.2 - 2.3.4} - Equivalent to &gt;=1.2.0 &lt;=2.3.4
     * </ul>
     */
    public static final String HYPHEN = format(Locale.ROOT, "^\\s*(%s)\\s+-\\s+(%s)\\s*$", XRANGE_PLAIN, XRANGE_PLAIN);

    /**
     * Regular expression for Ivy-style version range expressions.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code [1.0,2.0]} - Matches 1.0 &lt;= version &lt;= 2.0
     *   <li>{@code [1.0,2.0)} - Matches 1.0 &lt;= version &lt; 2.0
     *   <li>{@code (1.0,2.0]} - Matches 1.0 &lt; version &lt;= 2.0
     *   <li>{@code (1.0,2.0)} - Matches 1.0 &lt; version &lt; 2.0
     * </ul>
     */
    public static final String IVY =
            "^(\\[|\\]|\\()([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?\\,([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?(\\]|\\[|\\))$";

    /**
     * Regular expression for tilde-range expressions.
     *
     * <p>Allows patch-level changes if a minor version is specified, or minor-level changes if not.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code ~1.2.3} - Matches 1.2.3 up to, but not including 1.3.0
     *   <li>{@code ~1.2} - Matches 1.2.0 up to, but not including 1.3.0
     *   <li>{@code ~1} - Matches 1.0.0 up to, but not including 2.0.0
     * </ul>
     */
    public static final String TILDE = format(Locale.ROOT, "^(?:~>?)%s$", XRANGE_PLAIN);

    /**
     * Regular expression for greater-than/less-than operators.
     *
     * <p>Captures comparison operators: {@code <}, {@code <=}, {@code >}, {@code >=}, {@code =}
     */
    private static final String GTLT = "((?:<|>)?=?)";

    /**
     * Regular expression for X-Range expressions with operators.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code >1.2.3} - Matches versions greater than 1.2.3
     *   <li>{@code >=1.2.3} - Matches versions greater than or equal to 1.2.3
     *   <li>{@code <2.0.0} - Matches versions less than 2.0.0
     *   <li>{@code <=2.0.0} - Matches versions less than or equal to 2.0.0
     * </ul>
     */
    public static final String XRANGE = format(Locale.ROOT, "^%s\\s*%s$", GTLT, XRANGE_PLAIN);

    /**
     * Regular expression for comparator expressions.
     *
     * <p>Combines operators with version expressions.
     *
     * <p>Valid examples:
     *
     * <ul>
     *   <li>{@code =1.2.3} - Exact version 1.2.3
     *   <li>{@code >1.2.3} - Greater than 1.2.3
     *   <li>{@code >=1.2.3} - Greater than or equal to 1.2.3
     *   <li>{@code <2.0.0} - Less than 2.0.0
     *   <li>{@code <=2.0.0} - Less than or equal to 2.0.0
     *   <li>{@code } - Empty string (matches any version)
     * </ul>
     */
    public static final String COMPARATOR = format(Locale.ROOT, "^%s\\s*(%s)$|^$", GTLT, STRICT_PLAIN);
}
