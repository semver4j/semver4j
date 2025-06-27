package org.semver4j.processor;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.IVY;
import static org.semver4j.internal.Utils.*;
import static org.semver4j.range.Range.RangeOperator.*;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Processor for translating <a
 * href="https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html">Ivy ranges</a> into classic
 * version ranges.
 *
 * <p>Ivy ranges use a mathematical interval notation to specify version ranges. The processor converts these interval
 * notations into standard version ranges with greater-than, less-than, and equal-to operators.
 *
 * <p>This processor translates:
 *
 * <ul>
 *   <li>{@code [1.0,2.0]} to {@code ≥1.0.0 ≤2.0.0} - inclusive on both ends
 *   <li>{@code [1.0,2.0[} to {@code ≥1.0.0 <2.0.0} - inclusive lower bound, exclusive upper bound
 *   <li>{@code ]1.0,2.0]} to {@code >1.0.0 ≤2.0.0} - exclusive lower bound, inclusive upper bound
 *   <li>{@code ]1.0,2.0[} to {@code >1.0.0 <2.0.0} - exclusive on both ends
 *   <li>{@code [1.0,)} to {@code ≥1.0.0} - inclusive lower bound, unbounded above
 *   <li>{@code ]1.0,)} to {@code >1.0.0} - exclusive lower bound, unbounded above
 *   <li>{@code (,2.0]} to {@code ≤2.0.0} - unbounded below, inclusive upper bound
 *   <li>{@code (,2.0[} to {@code <2.0.0} - unbounded below, exclusive upper bound
 *   <li>{@code latest} to {@code ≥0.0.0} - matches any version
 *   <li>{@code latest.integration} to {@code ≥0.0.0} - matches any version
 * </ul>
 *
 * <p>If the {@code includePreRelease} flag is set to {@code true}, this processor will translate the same as if the
 * flag is not set, except for the following:
 *
 * <ul>
 *   <li>{@code latest} to {@code ≥0.0.0-0} - matches any version including pre-releases
 *   <li>{@code latest.integration} to {@code ≥0.0.0-0} - matches any version including pre-releases
 * </ul>
 *
 * <p>The notation follows these rules:
 *
 * <ul>
 *   <li>Square brackets {@code []} indicate inclusive bounds
 *   <li>Parentheses {@code ()} or angle brackets {@code ><} indicate exclusive bounds
 *   <li>An empty bound with a comma indicates an unbounded range in that direction
 *   <li>Partial versions are completed with zeros (e.g., {@code 1.2} becomes {@code 1.2.0})
 * </ul>
 *
 * @see Processor
 * @see <a href="https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html">Apache Ivy Version
 *     Matchers</a>
 */
public class IvyProcessor implements Processor {
    private static final String LATEST = "latest";
    private static final String LATEST_INTEGRATION = LATEST + ".integration";

    private static final Pattern PATTERN = compile(IVY);

    /**
     * Processes an Ivy range expression into a standard version range format.
     *
     * <p>This method handles both interval notation ranges and special keywords like {@code latest} and
     * {@code latest.integration}.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string if the input is a valid Ivy range, or {@code null} if this processor cannot
     *     handle the input
     */
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        if (range.equals(LATEST) || range.equals(LATEST_INTEGRATION)) {
            return includePreRelease ? ALL_RANGE_WITH_PRERELEASE : ALL_RANGE;
        }

        Matcher matcher = PATTERN.matcher(range);

        if (!matcher.matches()) {
            return null;
        }

        String openSign = matcher.group(1);

        int fromMajor = parseIntWithXSupport(matcher.group(2));
        int fromMinor = parseIntWithXSupport(matcher.group(3));
        int fromPatch = parseIntWithXSupport(matcher.group(4));

        int toMajor = parseIntWithXSupport(matcher.group(5));
        int toMinor = parseIntWithXSupport(matcher.group(6));
        int toPatch = parseIntWithXSupport(matcher.group(7));

        String closeSign = matcher.group(8);

        if (isX(fromMinor)) {
            fromMinor = 0;
        }
        if (isX(fromPatch)) {
            fromPatch = 0;
        }
        if (isX(toMinor)) {
            toMinor = 0;
        }
        if (isX(toPatch)) {
            toPatch = 0;
        }

        boolean openInclusive = isInclusiveRange(openSign);
        boolean closeInclusive = isInclusiveRange(closeSign);
        if (openInclusive && closeInclusive) {
            if (openSign.equals("[") && closeSign.equals("]")) {
                return format(
                        Locale.ROOT,
                        "%s%d.%d.%d %s%d.%d.%d",
                        GTE.asString(),
                        fromMajor,
                        fromMinor,
                        fromPatch,
                        LTE.asString(),
                        toMajor,
                        toMinor,
                        toPatch);
            } else if (openSign.equals("[") && closeSign.equals("[")) {
                return format(
                        Locale.ROOT,
                        "%s%d.%d.%d %s%d.%d.%d",
                        GTE.asString(),
                        fromMajor,
                        fromMinor,
                        fromPatch,
                        LT.asString(),
                        toMajor,
                        toMinor,
                        toPatch);
            } else if (openSign.equals("]") && closeSign.equals("]")) {
                return format(
                        Locale.ROOT,
                        "%s%d.%d.%d %s%d.%d.%d",
                        GT.asString(),
                        fromMajor,
                        fromMinor,
                        fromPatch,
                        LTE.asString(),
                        toMajor,
                        toMinor,
                        toPatch);
            } else if (openSign.equals("]") && closeSign.equals("[")) {
                return format(
                        Locale.ROOT,
                        "%s%d.%d.%d %s%d.%d.%d",
                        GT.asString(),
                        fromMajor,
                        fromMinor,
                        fromPatch,
                        LT.asString(),
                        toMajor,
                        toMinor,
                        toPatch);
            }
        } else if (closeSign.equals(")")) {
            if (openSign.equals("[")) {
                return format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), fromMajor, fromMinor, fromPatch);
            } else if (openSign.equals("]")) {
                return format(Locale.ROOT, "%s%d.%d.%d", GT.asString(), fromMajor, fromMinor, fromPatch);
            }
        } else if (openSign.equals("(")) {
            if (closeSign.equals("]")) {
                return format(Locale.ROOT, "%s%d.%d.%d", LTE.asString(), toMajor, toMinor, toPatch);
            } else if (closeSign.equals("[")) {
                return format(Locale.ROOT, "%s%d.%d.%d", LT.asString(), toMajor, toMinor, toPatch);
            }
        }

        return range;
    }

    private boolean isInclusiveRange(final String character) {
        return character.equals("[") || character.equals("]");
    }
}
