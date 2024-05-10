package org.semver4j.internal.range.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.internal.Tokenizers.CARET;
import static org.semver4j.internal.range.processor.RangesUtils.isNotBlank;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

/**
 * <p>Processor for translate <a href="https://github.com/npm/node-semver#caret-ranges-123-025-004">caret ranges</a>
 * into classic range.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>{@code ^1.2.3} to {@code ≥1.2.3 <2.0.0}</li>
 *     <li>{@code ^0.2.3} to {@code ≥0.2.3 <0.3.0}</li>
 * </ul>
 */
public class CaretProcessor implements Processor {
    @NotNull
    private static final Pattern pattern = compile(CARET);

    @Override
    public @Nullable String tryProcess(@NotNull String range) {
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

        if (minorIsX) {
            from = format(Locale.ROOT, "%s%d.0.0", GTE.asString(), major);
            to = format(Locale.ROOT, "%s%d.0.0", LT.asString(), (major + 1));
        } else if (patchIsX) {
            if (major == 0) {
                from = format(Locale.ROOT, "%s%d.%d.0", GTE.asString(), major, minor);
                to = format(Locale.ROOT, "%s%d.%d.0", LT.asString(), major, (minor + 1));
            } else {
                from = format(Locale.ROOT, "%s%d.%d.0", GTE.asString(), major, minor);
                to = format(Locale.ROOT, "%s%d.0.0", LT.asString(), (major + 1));
            }
        } else if (isNotBlank(preRelease)) {
            if (major == 0) {
                if (minor == 0) {
                    from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                    to = format(Locale.ROOT, "%s%d.%d.%d", LT.asString(), major, minor, (path + 1));
                } else {
                    from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                    to = format(Locale.ROOT, "%s%d.%d.0", LT.asString(), major, (minor + 1));
                }
            } else {
                from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
                to = format(Locale.ROOT, "%s%d.0.0", LT.asString(), (major + 1));
            }
        } else {
            if (major == 0) {
                if (minor == 0) {
                    from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
                    to = format(Locale.ROOT, "%s%d.%d.%d", LT.asString(), major, minor, (path + 1));
                } else {
                    from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
                    to = format(Locale.ROOT, "%s%d.%d.0", LT.asString(), major, (minor + 1));
                }
            } else {
                from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
                to = format(Locale.ROOT, "%s%d.0.0", LT.asString(), (major + 1));
            }
        }

        return format(Locale.ROOT, "%s %s", from, to);
    }
}
