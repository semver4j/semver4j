package org.semver4j.internal;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for coercing arbitrary version strings into valid semantic version format.
 *
 * <p>This class extracts numeric version components from strings and formats them as valid semantic versions in the
 * format {@code major.minor.patch}.
 */
public class Coerce {
    /**
     * Regular expression pattern used to extract version numbers from strings.
     *
     * <p>The pattern identifies up to three numeric groups that represent {@code major}, {@code minor}, and
     * {@code patch} version components. It handles leading zeros and various delimiter formats.
     */
    private static final Pattern PATTERN =
            compile("(^|\\D)0*(\\d{1,16})(?:\\.0*(\\d{1,16}))?(?:\\.0*(\\d{1,16}))?(?:$|\\D)");

    /** Private constructor to prevent instantiation of this utility class. */
    private Coerce() {}

    /**
     * Attempts to convert an arbitrary version string into a valid semantic version format.
     *
     * <p>The method extracts numeric components from the input string and formats them as a semantic version. If
     * {@code minor} or {@code patch} components are not found in the input, they default to {@code 0}.
     *
     * @param version The input version string to be coerced
     * @return A formatted semantic version string in the format {@code major.minor.patch}, or {@code null} if no valid
     *     version components could be extracted
     */
    public static @Nullable String coerce(String version) {
        Matcher matcher = PATTERN.matcher(version);

        if (matcher.find()) {
            String coercedMajor = matcher.group(2);
            String coercedMinor = Optional.ofNullable(matcher.group(3)).orElse("0");
            String coercedPath = Optional.ofNullable(matcher.group(4)).orElse("0");

            return format(Locale.ROOT, "%s.%s.%s", coercedMajor, coercedMinor, coercedPath);
        }

        return null;
    }
}
