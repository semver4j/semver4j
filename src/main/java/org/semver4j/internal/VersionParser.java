package org.semver4j.internal;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.STRICT;

import java.math.BigInteger;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;
import org.semver4j.SemverException;

/**
 * A utility class for parsing semantic version strings according to the SemVer specification.
 *
 * <p>This class provides functionality to parse a version string into its components: {@code major}, {@code minor},
 * {@code patch}, {@code pre-release} identifiers, and {@code build} metadata.
 *
 * <p>The parser follows strict SemVer rules and validates input against the standard pattern.
 *
 * @see <a href="https://semver.org/">Semantic Versioning Specification</a>
 */
public class VersionParser {
    private static final Pattern PATTERN = compile(STRICT);
    private static final BigInteger MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);

    /** Private constructor to prevent instantiation of this utility class */
    private VersionParser() {}

    /**
     * Parses a semantic version string into its component parts.
     *
     * @param version the version string to parse
     * @return a {@link Version} object containing the parsed components
     * @throws SemverException if the version string is not a valid semantic version
     */
    public static Version parse(String version) {
        Matcher matcher = PATTERN.matcher(version);

        if (!matcher.matches()) {
            throw new SemverException(format(Locale.ROOT, "Version [%s] is not valid semver.", version));
        }

        int major = parseInt(matcher.group(1));
        int minor = parseInt(matcher.group(2));
        int patch = parseInt(matcher.group(3));
        List<String> preRelease = convertToList(matcher.group(4));
        List<String> build = convertToList(matcher.group(5));

        return new Version(major, minor, patch, preRelease, build);
    }

    private static int parseInt(@Nullable String maybeInt) {
        if (maybeInt == null) {
            throw new SemverException("Value is null and cannot be parsed as an integer.");
        }

        BigInteger secureNumber = new BigInteger(maybeInt);
        if (MAX_INT.compareTo(secureNumber) < 0) {
            throw new SemverException(format(Locale.ROOT, "Value [%s] is too big.", maybeInt));
        }

        /*
         * Do not use BigInteger.intValueExact() because it is not available on Android with API < 31.
         * The use of BigInteger.intValue() here is always valid since the above check guarantees that
         * the BigInteger is not too big to fit in an int (non-negative integer <= Integer.MAX_VALUE).
         * In other words, BigInteger.intValueExact() would never throw, meaning, it is not necessary.
         */
        return secureNumber.intValue();
    }

    private static List<String> convertToList(@Nullable String toList) {
        return toList == null ? List.of() : List.of(toList.split("\\."));
    }

    /**
     * A record representing a parsed semantic version with its component parts.
     *
     * <p>Includes {@code major}, {@code minor}, and {@code pat h} version numbers, as well as optional
     * {@code pre-release} identifiers and {@code build} metadata.
     */
    public record Version(int major, int minor, int patch, List<String> preRelease, List<String> build) {
        Version(int major, int minor, int patch) {
            this(major, minor, patch, List.of(), List.of());
        }
    }
}
