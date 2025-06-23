package org.semver4j.processor;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.internal.Tokenizers.CARET;
import static org.semver4j.processor.RangesUtils.*;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

/**
 * Processor for translate <a href="https://github.com/npm/node-semver#caret-ranges-123-025-004">caret ranges</a> into
 * classic range. <br>
 * Translates:
 *
 * <ul>
 *   <li>{@code ^1.2.3} to {@code ≥1.2.3 <2.0.0}
 *   <li>{@code ^1.2} to {@code ≥1.2.0 <2.0.0}
 *   <li>{@code ^1} to {@code ≥1.0.0 < 2.0.0}
 *   <li>{@code ^0.2.3} to {@code ≥0.2.3 <0.3.0}
 * </ul>
 *
 * <p>If the prerelease flag is set to true, will translate:
 *
 * <ul>
 *   <li>{@code ^1.2.3} to {@code ≥1.2.3 <2.0.0-0}
 *   <li>{@code ^1.2} to {@code ≥1.2.0-0 <2.0.0-0}
 *   <li>{@code ^1} to {@code ≥1.0.0-0 < 2.0.0-0}
 *   <li>{@code ^0.2.3} to {@code ≥0.2.3 <0.3.0-0}
 * </ul>
 */
@NullMarked
public class CaretProcessor implements Processor {
    private static final Pattern pattern = compile(CARET);

    @Override
    @Nullable
    public String process(String range, boolean includePrerelease) {
        Matcher matcher = pattern.matcher(range);

        if (!matcher.matches()) {
            return null;
        }

        int major = parseIntWithXSupport(matcher.group(1));
        int minor = parseIntWithXSupport(matcher.group(2));
        int path = parseIntWithXSupport(matcher.group(3));
        String preRelease = matcher.group(4);

        boolean minorIsX = isX(minor);
        boolean patchIsX = isX(path);

        String from;
        String to;
        String prerelease = includePrerelease ? Processor.LOWEST_PRERELEASE : "";

        if (minorIsX) {
            from = format(Locale.ROOT, "%s%d.0.0%s", GTE.asString(), major, prerelease);
            to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), prerelease);
        } else if (patchIsX) {
            if (major == 0) {
                from = format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), major, minor, prerelease);
                to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), prerelease);
            } else {
                from = format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), major, minor, prerelease);
                to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), prerelease);
            }
        } else if (isNotBlank(preRelease)) {
            if (major == 0) {
                if (minor == 0) {
                    from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                    to = format(Locale.ROOT, "%s%d.%d.%d%s", LT.asString(), major, minor, (path + 1), prerelease);
                } else {
                    from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                    to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), prerelease);
                }
            } else {
                from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), prerelease);
            }
        } else {
            if (major == 0) {
                if (minor == 0) {
                    from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
                    to = format(Locale.ROOT, "%s%d.%d.%d%s", LT.asString(), major, minor, (path + 1), prerelease);
                } else {
                    from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
                    to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), prerelease);
                }
            } else {
                from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
                to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), prerelease);
            }
        }

        return format(Locale.ROOT, "%s %s", from, to);
    }
}
