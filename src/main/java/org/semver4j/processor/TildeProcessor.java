package org.semver4j.processor;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.TILDE;
import static org.semver4j.internal.Utils.*;
import static org.semver4j.range.Range.RangeOperator.GTE;
import static org.semver4j.range.Range.RangeOperator.LT;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;
import org.semver4j.range.Range.RangeOperator;

/**
 * Processor for translating <a href="https://github.com/npm/node-semver#tilde-ranges-123-12-1">tilde ranges</a> into
 * classic version ranges.
 *
 * <p>Tilde ranges specify patch-level changes if a minor version is specified, or minor-level changes if not. The
 * processor converts these ranges into standard version ranges with greater-than-or-equal-to and less-than operators.
 *
 * <p>This processor translates:
 *
 * <ul>
 *   <li>{@code ~1.2.3} to {@code ≥1.2.3 <1.3.0} - allows patch-level changes
 *   <li>{@code ~1.2} to {@code ≥1.2.0 <1.3.0} - allows changes to patch version only
 *   <li>{@code ~1} to {@code ≥1.0.0 <2.0.0} - allows changes to minor and patch versions
 *   <li>{@code ~0.2.3} to {@code ≥0.2.3 <0.3.0} - special case for 0.x.x: allows patch-level changes
 *   <li>{@code ~0.2} to {@code ≥0.2.0 <0.3.0} - special case for 0.x: allows changes to patch version only
 *   <li>{@code ~0} to {@code ≥0.0.0 <1.0.0} - allows changes to minor and patch versions
 * </ul>
 *
 * <p>If the {@code includePreRelease} flag is set to {@code true}, this processor will translate:
 *
 * <ul>
 *   <li>{@code ~1.2.3} to {@code ≥1.2.3 <1.3.0-0} - includes pre-releases before 1.3.0
 *   <li>{@code ~1.2} to {@code ≥1.2.0-0 <1.3.0-0} - includes pre-releases on both ends
 *   <li>{@code ~1} to {@code ≥1.0.0-0 <2.0.0-0} - includes pre-releases on both ends
 *   <li>{@code ~0.2.3} to {@code ≥0.2.3 <0.3.0-0} - includes pre-releases before 0.3.0
 *   <li>{@code ~0.2} to {@code ≥0.2.0-0 <0.3.0-0} - includes pre-releases on both ends
 *   <li>{@code ~0} to {@code ≥0.0.0-0 <1.0.0-0} - includes pre-releases on both ends
 * </ul>
 *
 * <p>Special cases:
 *
 * <ul>
 *   <li>If the version contains a pre-release identifier (e.g., {@code ~1.2.3-beta}), the lower bound will include that
 *       pre-release version
 *   <li>X-ranges (versions containing {@code x}, {@code X}, or {@code *}) are treated as partial versions
 *   <li>Missing minor or patch values are treated as zeros
 * </ul>
 *
 * <p>The tilde operator is similar to the caret operator, but more conservative with respect to breaking changes. While
 * caret ranges permit changes that do not modify the left-most non-zero digit, tilde ranges only permit changes to the
 * patch version (right-most digit), unless only the major version is specified.
 *
 * @see Processor
 * @see <a href="https://github.com/npm/node-semver#tilde-ranges-123-12-1">npm SemVer Tilde Ranges</a>
 */
public class TildeProcessor implements Processor {
    private static final Pattern PATTERN = compile(TILDE);

    /**
     * Processes a tilde range expression into a standard version range format.
     *
     * <p>This method extracts the version components from the tilde range and constructs an appropriate version range
     * expression based on the specified major, minor, and patch versions.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string if the input is a valid tilde range, or {@code null} if this processor cannot
     *     handle the input
     */
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        Matcher matcher = PATTERN.matcher(range);

        if (!matcher.matches()) {
            return null;
        }

        int major = parseIntWithXSupport(matcher.group(1));
        int minor = parseIntWithXSupport(matcher.group(2));
        int patch = parseIntWithXSupport(matcher.group(3));
        String explicitPreRelease = matcher.group(4);
        String preRelease = includePreRelease ? LOWEST_PRE_RELEASE : EMPTY;

        return createVersionRange(major, minor, patch, explicitPreRelease, preRelease);
    }

    private String createVersionRange(int major, int minor, int patch, String explicitPreRelease, String preRelease) {
        String from;
        String to;

        if (isX(minor)) {
            // ~1 becomes ≥1.0.0 <2.0.0
            from = formatVersion(GTE, major, 0, 0, preRelease);
            to = formatVersion(LT, major + 1, 0, 0, preRelease);
        } else if (isX(patch)) {
            // ~1.2 becomes ≥1.2.0 <1.3.0
            from = formatVersion(GTE, major, minor, 0, preRelease);
            to = formatVersion(LT, major, minor + 1, 0, preRelease);
        } else if (isNotBlank(explicitPreRelease)) {
            // ~1.2.3-beta becomes ≥1.2.3-beta <1.3.0
            from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, patch, explicitPreRelease);
            to = formatVersion(LT, major, minor + 1, 0, preRelease);
        } else {
            // ~1.2.3 becomes ≥1.2.3 <1.3.0
            from = formatVersion(GTE, major, minor, patch, EMPTY);
            to = formatVersion(LT, major, minor + 1, 0, preRelease);
        }

        return format(Locale.ROOT, "%s %s", from, to);
    }

    private String formatVersion(RangeOperator operator, int major, int minor, int patch, String preRelease) {
        return format(Locale.ROOT, "%s%d.%d.%d%s", operator.asString(), major, minor, patch, preRelease);
    }
}
