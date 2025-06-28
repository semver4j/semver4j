package org.semver4j.processor;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.CARET;
import static org.semver4j.internal.Utils.*;
import static org.semver4j.range.Range.RangeOperator.GTE;
import static org.semver4j.range.Range.RangeOperator.LT;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Processor for translating <a href="https://github.com/npm/node-semver#caret-ranges-123-025-004">caret ranges</a> into
 * classic version ranges.
 *
 * <p>The caret ({@code ^}) allows changes that do not modify the left-most non-zero digit in the version, which aligns
 * with the SemVer principle of backward compatibility for minor and patch changes within a major version.
 *
 * <p>This processor translates:
 *
 * <ul>
 *   <li>{@code ^1.2.3} to {@code ≥1.2.3 <2.0.0} - allowing minor and patch changes
 *   <li>{@code ^1.2} to {@code ≥1.2.0 <2.0.0} - treating missing patch as 0
 *   <li>{@code ^1} to {@code ≥1.0.0 <2.0.0} - treating missing minor and patch as 0
 *   <li>{@code ^0.2.3} to {@code ≥0.2.3 <0.3.0} - when major is 0, only allow patch changes
 *   <li>{@code ^0.0.3} to {@code ≥0.0.3 <0.0.4} - when major and minor are 0, only allow that exact version
 * </ul>
 *
 * <p>If the {@code includePreRelease} flag is set to {@code true}, this processor will translate:
 *
 * <ul>
 *   <li>{@code ^1.2.3} to {@code ≥1.2.3 <2.0.0-0} - also including pre-releases before 2.0.0
 *   <li>{@code ^1.2} to {@code ≥1.2.0-0 <2.0.0-0} - including all pre-releases
 *   <li>{@code ^1} to {@code ≥1.0.0-0 <2.0.0-0} - including all pre-releases
 *   <li>{@code ^0.2.3} to {@code ≥0.2.3 <0.3.0-0} - including pre-releases before 0.3.0
 *   <li>{@code ^0.0.3} to {@code ≥0.0.3 <0.0.4-0} - including pre-releases before 0.0.4
 * </ul>
 *
 * <p>Special cases:
 *
 * <ul>
 *   <li>When a {@code pre-release} is specified (e.g., {@code ^1.2.3-beta}), the lower bound includes that specific
 *       {@code pre-release} version
 *   <li>When {@code major=0}, incrementing only happens at the next level to maintain the strictness requirement of
 *       0.x.y versions
 * </ul>
 *
 * @see Processor
 * @see <a href="https://github.com/npm/node-semver#caret-ranges-123-025-004">npm SemVer Caret Ranges</a>
 */
public class CaretProcessor implements Processor {
    private static final Pattern PATTERN = compile(CARET);

    /**
     * Processes a caret range expression into a standard version range format.
     *
     * <p>This method extracts the version components from the caret range and constructs an appropriate version range
     * based on the SemVer compatibility rules.
     *
     * @param range the version range string to process
     * @param includePreRelease whether to include pre-release versions in the range
     * @return the processed range string if the input is a valid caret range, or {@code null} if this processor cannot
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
        String preRelease = matcher.group(4);

        String from;
        String to;
        String preReleaseMarker = includePreRelease ? LOWEST_PRE_RELEASE : EMPTY;

        if (isX(minor)) {
            from = format(Locale.ROOT, "%s%d.0.0%s", GTE.asString(), major, preReleaseMarker);
        } else if (isX(patch)) {
            from = format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), major, minor, preReleaseMarker);
        } else if (isNotBlank(preRelease)) {
            from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, patch, preRelease);
        } else {
            from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, patch);
        }

        if (major > 0) {
            to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), preReleaseMarker);
        } else if (minor > 0) {
            to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), preReleaseMarker);
        } else {
            to = format(Locale.ROOT, "%s%d.%d.%d%s", LT.asString(), major, minor, (patch + 1), preReleaseMarker);
        }

        return format(Locale.ROOT, "%s %s", from, to);
    }
}
