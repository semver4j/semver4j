package org.semver4j.internal;

import java.util.Locale;

import static java.lang.String.format;

/**
 * List of regexp that helps with tokenizing and parsing <a href="https://semver.org">semver</a> strings.
 */
@SuppressWarnings("checkstyle:DeclarationOrder")
public class Tokenizers {
    private Tokenizers() {
    }

    /**
     * Not negative integers without leading zeroes.<br>
     * See <a href="https://semver.org/#spec-item-2">semver.org#spec-item-2</a>
     */
    private static final String NUMERIC_IDENTIFIER = "0|[1-9]\\d*";

    private static final String NON_NUMERIC_IDENTIFIER = "\\d*[a-zA-Z-][a-zA-Z0-9-]*";

    /**
     * Dot separated numeric version identifiers (<b>MAJOR</b>.<b>MINOR</b>.<b>PATCH</b>).<br>
     * See <a href="https://semver.org/#summary">semver.org#summary</a>
     */
    private static final String MAIN_VERSION = format(Locale.ROOT, "(%s)\\.(%s)\\.(%s)", NUMERIC_IDENTIFIER, NUMERIC_IDENTIFIER, NUMERIC_IDENTIFIER);

    private static final String PRERELEASE_IDENTIFIER = format(Locale.ROOT, "(?:%s|%s)", NUMERIC_IDENTIFIER, NON_NUMERIC_IDENTIFIER);

    /**
     * Prerelease identifier forwarded by hyphen with appended series of dot-separated identifiers.<br>
     * See <a href="https://semver.org/#spec-item-9">semver.org#spec-item-9</a>
     */
    private static final String PRERELEASE = format(Locale.ROOT, "(?:-(%s(?:\\.%s)*))", PRERELEASE_IDENTIFIER, PRERELEASE_IDENTIFIER);

    private static final String BUILD_IDENTIFIER = "[0-9A-Za-z-]+";

    private static final String BUILD = format(Locale.ROOT, "(?:\\+(%s(?:\\.%s)*))", BUILD_IDENTIFIER, BUILD_IDENTIFIER);

    private static final String STRICT_PLAIN = format(Locale.ROOT, "v?%s%s?%s?", MAIN_VERSION, PRERELEASE, BUILD);

    /**
     * Fully, completely valid semver value.<br>
     * Required section is:<br>
     * <code>
     * X.Y.Z
     * </code>
     * <br>
     * Example with not required sections:<br>
     * <code>
     * X.Y.X-beta.1+sha1234
     * </code>
     */
    public static final String STRICT = format(Locale.ROOT, "^%s$", STRICT_PLAIN);

    /**
     * <p>{@code x}, {@code X} and {@code *} are for X-Ranges.</p>
     * <p>{@code +} is for Ivy ranges.</p>
     */
    private static final String XRANGE_IDENTIFIER = format(Locale.ROOT, "%s|x|X|\\*|\\+", NUMERIC_IDENTIFIER);

    private static final String XRANGE_PLAIN = format(Locale.ROOT, "[v=\\s]*(%s)(?:\\.(%s)(?:\\.(%s)(?:%s)?%s?)?)?", XRANGE_IDENTIFIER, XRANGE_IDENTIFIER, XRANGE_IDENTIFIER, PRERELEASE, BUILD);

    private static final String LONE_CARET = "(?:\\^)";

    public static final String CARET = format(Locale.ROOT, "^%s%s$", LONE_CARET, XRANGE_PLAIN);

    public static final String HYPHEN = format(Locale.ROOT, "^\\s*(%s)\\s+-\\s+(%s)\\s*$", XRANGE_PLAIN, XRANGE_PLAIN);

    public static final String IVY = "^(\\[|\\]|\\()([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?\\,([0-9]+)?\\.?([0-9]+)?\\.?([0-9]+)?(\\]|\\[|\\))$";

    public static final String TILDE = format(Locale.ROOT, "^(?:~>?)%s$", XRANGE_PLAIN);

    private static final String GLTL = "((?:<|>)?=?)";

    public static final String XRANGE = format(Locale.ROOT, "^%s\\s*%s$", GLTL, XRANGE_PLAIN);

    public static final String COMPARATOR = format(Locale.ROOT, "^%s\\s*(%s)$|^$", GLTL, STRICT_PLAIN);

    public static final String COERCE = "(^|[^\\d])(\\d{1,16})(?:\\.(\\d{1,16}))?(?:\\.(\\d{1,16}))?(?:$|[^\\d])";
}
