package org.semver4j.processor;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.HYPHEN;
import static org.semver4j.internal.Utils.*;
import static org.semver4j.range.Range.RangeOperator.*;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Processor for translating <a href="https://github.com/npm/node-semver#hyphen-ranges-xyz---abc">hyphen ranges</a> into
 * classic version ranges.
 *
 * <p>Hyphen ranges specify an inclusive set of versions, using two version numbers separated by a hyphen. The processor
 * converts these inclusive ranges into standard version ranges with greater-than-or-equal-to and less-than operators.
 *
 * <p>This processor translates:
 *
 * <ul>
 *   <li>{@code 1.2.3 - 2.3.4} to {@code ≥1.2.3 <2.3.5} - fully specified versions on both sides
 *   <li>{@code 1.2 - 2.3.4} to {@code ≥1.2.0 <2.3.5} - partial version on left side
 *   <li>{@code 1.2.3 - 2.3} to {@code ≥1.2.3 <2.4.0} - partial version on right side
 *   <li>{@code 1.2.3 - 2} to {@code ≥1.2.3 <3.0.0} - major-only version on right side
 * </ul>
 *
 * <p>If the {@code includePreRelease} flag is set to {@code true}, this processor will translate:
 *
 * <ul>
 *   <li>{@code 1.2.3 - 2.3.4} to {@code ≥1.2.3 <2.3.5-0} - including pre-release versions
 *   <li>{@code 1.2 - 2.3.4} to {@code ≥1.2.0-0 <2.3.5-0} - including pre-release versions on both sides
 *   <li>{@code 1.2.3 - 2.3} to {@code ≥1.2.3 <2.4.0-0} - including pre-release versions before 2.4.0
 *   <li>{@code 1.2.3 - 2} to {@code ≥1.2.3 <3.0.0-0} - including pre-release versions before 3.0.0
 * </ul>
 *
 * <p>Special cases:
 *
 * <ul>
 *   <li>If the right version contains an explicit pre-release identifier, it's treated as an inclusive upper bound (≤)
 *   <li>Partial versions are completed with zeros (e.g., {@code 1.2} becomes {@code 1.2.0})
 *   <li>X-ranges (versions containing {@code x}, {@code X}, or {@code *}) are treated as partial versions
 * </ul>
 *
 * @see Processor
 * @see <a href="https://github.com/npm/node-semver#hyphen-ranges-xyz---abc">npm SemVer Hyphen Ranges</a>
 */
public class HyphenProcessor implements Processor {
    private static final Pattern PATTERN = compile(HYPHEN);

    /**
     * Processes a hyphen range expression into a standard version range format.
     *
     * <p>This method extracts the version components from both sides of the hyphen range and constructs an appropriate
     * version range expression.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string if the input is a valid hyphen range, or {@code null} if this processor cannot
     *     handle the input
     */
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        Matcher matcher = PATTERN.matcher(range);

        if (!matcher.matches()) {
            return null;
        }

        String rangeFrom = getRangeFrom(matcher, includePreRelease);
        String rangeTo = getRangeTo(matcher, includePreRelease);

        return format(Locale.ROOT, "%s %s", rangeFrom, rangeTo);
    }

    private String getRangeFrom(Matcher matcher, boolean includePreRelease) {
        String from = matcher.group(1);
        int fromMajor = parseIntWithXSupport(matcher.group(2));
        int fromMinor = parseIntWithXSupport(matcher.group(3));
        int fromPatch = parseIntWithXSupport(matcher.group(4));

        String preRelease = includePreRelease ? LOWEST_PRE_RELEASE : EMPTY;

        if (isX(fromMinor)) {
            return format(Locale.ROOT, "%s%d.0.0%s", GTE.asString(), fromMajor, preRelease);
        } else if (isX(fromPatch)) {
            return format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), fromMajor, fromMinor, preRelease);
        } else {
            return format(Locale.ROOT, "%s%s", GTE.asString(), from);
        }
    }

    private String getRangeTo(Matcher matcher, boolean includePreRelease) {
        int toMajor = parseIntWithXSupport(matcher.group(8));
        int toMinor = parseIntWithXSupport(matcher.group(9));
        int toPatch = parseIntWithXSupport(matcher.group(10));
        String explicitPreRelease = matcher.group(11);

        String preRelease = includePreRelease ? LOWEST_PRE_RELEASE : EMPTY;

        if (isX(toMinor)) {
            return format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), toMajor + 1, preRelease);
        } else if (isX(toPatch)) {
            return format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), toMajor, toMinor + 1, preRelease);
        } else if (isNotBlank(explicitPreRelease)) {
            return format(
                    Locale.ROOT, "%s%d.%d.%d%s", LTE.asString(), toMajor, toMinor, toPatch, "-" + explicitPreRelease);
        } else {
            return format(Locale.ROOT, "%s%d.%d.%d%s", LT.asString(), toMajor, toMinor, toPatch + 1, preRelease);
        }
    }
}
