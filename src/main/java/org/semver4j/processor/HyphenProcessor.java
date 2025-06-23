package org.semver4j.processor;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.*;
import static org.semver4j.internal.RangesUtils.*;
import static org.semver4j.internal.Tokenizers.HYPHEN;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Processor for translate <a href="https://github.com/npm/node-semver#hyphen-ranges-xyz---abc">hyphen ranges</a> into a
 * classic ranges. <br>
 * Translates:
 *
 * <ul>
 *   <li>{@code 1.2.3 - 2.3.4} to {@code ≥1.2.3 <2.3.5}
 *   <li>{@code 1.2 - 2.3.4} to {@code ≥1.2.0 <2.3.5}
 *   <li>{@code 1.2.3 - 2.3} to {@code ≥1.2.3 <2.4.0}
 *   <li>{@code 1.2.3 - 2} to {@code ≥1.2.3 <3.0.0}
 * </ul>
 *
 * <p>If the prerelease flag is set to true, will translate:
 *
 * <ul>
 *   <li>{@code 1.2.3 - 2.3.4} to {@code ≥1.2.3 <2.3.5-0}
 *   <li>{@code 1.2 - 2.3.4} to {@code ≥1.2.0-0 <2.3.5-0}
 *   <li>{@code 1.2.3 - 2.3} to {@code ≥1.2.3 <2.4.0-0}
 *   <li>{@code 1.2.3 - 2} to {@code ≥1.2.3 <3.0.0-0}
 * </ul>
 */
@NullMarked
public class HyphenProcessor implements Processor {
    private static final Pattern pattern = compile(HYPHEN);

    @Override
    @Nullable
    public String process(String range, boolean includePreRelease) {
        Matcher matcher = pattern.matcher(range);

        if (!matcher.matches()) {
            return null;
        }

        String rangeFrom = getRangeFrom(matcher, includePreRelease);
        String rangeTo = getRangeTo(matcher, includePreRelease);

        return format(Locale.ROOT, "%s %s", rangeFrom, rangeTo);
    }

    private String getRangeFrom(final Matcher matcher, boolean includePrerelease) {
        String from = matcher.group(1);

        int fromMajor = parseIntWithXSupport(matcher.group(2));
        int fromMinor = parseIntWithXSupport(matcher.group(3));
        int fromPatch = parseIntWithXSupport(matcher.group(4));

        String prerelease = includePrerelease ? Processor.LOWEST_PRERELEASE : "";

        boolean minorIsX = isX(fromMinor);
        boolean patchIsX = isX(fromPatch);

        if (minorIsX) {
            return format(Locale.ROOT, "%s%d.0.0%s", GTE.asString(), fromMajor, prerelease);
        } else {
            if (patchIsX) {
                return format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), fromMajor, fromMinor, prerelease);
            } else {
                return format(Locale.ROOT, "%s%s", GTE.asString(), from);
            }
        }
    }

    private String getRangeTo(final Matcher matcher, boolean includePrerelease) {
        int toMajor = parseIntWithXSupport(matcher.group(8));
        int toMinor = parseIntWithXSupport(matcher.group(9));
        int toPatch = parseIntWithXSupport(matcher.group(10));

        @Nullable String preRelease = matcher.group(11);
        String prerelease = includePrerelease ? Processor.LOWEST_PRERELEASE : "";

        boolean minorIsX = isX(toMinor);
        boolean patchIsX = isX(toPatch);

        if (minorIsX) {
            return format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (toMajor + 1), prerelease);
        } else {
            if (patchIsX) {
                return format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), toMajor, (toMinor + 1), prerelease);
            } else {
                if (isNotBlank(preRelease)) {
                    return format(
                            Locale.ROOT, "%s%d.%d.%d%s", LTE.asString(), toMajor, toMinor, toPatch, "-" + preRelease);
                } else {
                    return format(
                            Locale.ROOT, "%s%d.%d.%d%s", LT.asString(), toMajor, toMinor, (toPatch + 1), prerelease);
                }
            }
        }
    }
}
