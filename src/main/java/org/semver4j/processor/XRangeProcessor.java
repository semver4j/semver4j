package org.semver4j.processor;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.XRANGE;
import static org.semver4j.internal.Utils.*;
import static org.semver4j.range.Range.RangeOperator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Processor for translating <a href="https://github.com/npm/node-semver#x-ranges-12x-1x-12-">X-Ranges</a> into classic
 * version ranges.
 *
 * <p>X-Ranges use the special character {@code 'x'} or {@code '*'} (or even the uppercase {@code 'X'}) as a wildcard to
 * specify that any value is acceptable in that position. The processor converts these wildcards into appropriate
 * version ranges with greater-than-or-equal-to and less-than operators.
 *
 * <p>This processor translates:
 *
 * <ul>
 *   <li>{@code *} or {@code x} to {@code ≥0.0.0} - any version
 *   <li>{@code 1.x} or {@code 1.X} or {@code 1.*} to {@code ≥1.0.0 <2.0.0} - any version with major version 1
 *   <li>{@code 1.2.x} or {@code 1.2.*} to {@code ≥1.2.0 <1.3.0} - any version with major version 1 and minor version 2
 *   <li>{@code >1.2.x} to {@code ≥1.3.0} - greater than any version with major version 1 and minor version 2
 *   <li>{@code >1.x} to {@code ≥2.0.0} - greater than any version with major version 1
 *   <li>{@code <=1.2.x} to {@code <1.3.0} - less than or equal to any version with major version 1 and minor version 2
 * </ul>
 *
 * <p>If the {@code includePreRelease} flag is set to {@code true}, this processor will include pre-release versions in
 * the resulting ranges by appending the {@code -0} suffix to version boundaries where appropriate.
 *
 * <p>Special cases:
 *
 * <ul>
 *   <li>When a comparison operator (like {@code >}, {@code <}, {@code >=}, or {@code <=}) is included, the x-range is
 *       treated differently depending on which version component contains the wildcard
 *   <li>Missing version components are treated as wildcards (e.g., {@code 1} is equivalent to {@code 1.x.x})
 *   <li>Multiple space-separated x-ranges are combined into a single range expression
 *   <li>The {@code =} operator with an x-range is treated the same as having no operator
 * </ul>
 *
 * @see Processor
 * @see <a href="https://github.com/npm/node-semver#x-ranges-12x-1x-12-">npm SemVer X-Ranges</a>
 */
public class XRangeProcessor implements Processor {
    private static final Pattern PATTERN = compile(XRANGE);

    /**
     * Processes an X-Range expression into a standard version range format.
     *
     * <p>This method handles single X-Range expressions as well as multiple space-separated X-Range expressions. It
     * extracts the version components and any comparison operators, then constructs appropriate version range
     * expressions.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string if the input contains valid X-Range expressions, or {@code null} if this
     *     processor cannot handle the input
     */
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        String preReleaseSuffix = includePreRelease ? LOWEST_PRE_RELEASE : EMPTY;
        String[] rangeVersions = range.split("\\s+");

        List<String> processedRanges = new ArrayList<>();
        for (String rangeVersion : rangeVersions) {
            String processedRange = processRangeVersion(rangeVersion, preReleaseSuffix);
            if (processedRange != null) {
                processedRanges.add(processedRange);
            }
        }

        if (processedRanges.isEmpty()) {
            return null;
        }

        return join(SPACE, processedRanges);
    }

    private @Nullable String processRangeVersion(String rangeVersion, String preReleaseSuffix) {
        Matcher matcher = PATTERN.matcher(rangeVersion);
        if (!matcher.matches()) {
            return null;
        }

        String fullRange = matcher.group(0);
        String compareSign = matcher.group(1);
        int major = parseIntWithXSupport(matcher.group(2));
        int minor = parseIntWithXSupport(matcher.group(3));
        int patch = parseIntWithXSupport(matcher.group(4));

        if (compareSign.equals(EQ.asString()) && isX(patch)) {
            compareSign = EMPTY;
        }

        if (!compareSign.isEmpty() && isX(patch)) {
            return processWithComparisonOperator(compareSign, major, minor, preReleaseSuffix);
        } else if (isX(minor)) {
            return processMajorOnly(major, preReleaseSuffix);
        } else if (isX(patch)) {
            return processMajorMinor(major, minor, preReleaseSuffix);
        } else {
            return fullRange;
        }
    }

    private String processWithComparisonOperator(String compareSign, int major, int minor, String preReleaseSuffix) {

        if (compareSign.equals(GT.asString())) {
            compareSign = GTE.asString();
            if (isX(minor)) {
                major = major + 1;
                minor = 0;
            } else {
                minor = minor + 1;
            }
        } else if (compareSign.equals(LTE.asString())) {
            compareSign = LT.asString();
            if (isX(minor)) {
                major = major + 1;
                minor = 0;
            } else {
                minor = minor + 1;
            }
        } else if (isX(minor)) {
            minor = 0;
        }

        return formatVersion(compareSign, major, minor, preReleaseSuffix);
    }

    private String processMajorOnly(int major, String preReleaseSuffix) {
        // 1.x or 1.X or 1.* becomes ≥1.0.0 <2.0.0
        String from = formatVersion(GTE.asString(), major, 0, preReleaseSuffix);
        String to = formatVersion(LT.asString(), major + 1, 0, preReleaseSuffix);
        return from + SPACE + to;
    }

    private String processMajorMinor(int major, int minor, String preReleaseSuffix) {
        // 1.2.x or 1.2.* becomes ≥1.2.0 <1.3.0
        String from = formatVersion(GTE.asString(), major, minor, preReleaseSuffix);
        String to = formatVersion(LT.asString(), major, minor + 1, preReleaseSuffix);
        return from + SPACE + to;
    }

    private String formatVersion(String operator, int major, int minor, String preReleaseSuffix) {
        return format(Locale.ROOT, "%s%d.%d.%d%s", operator, major, minor, 0, preReleaseSuffix);
    }
}
