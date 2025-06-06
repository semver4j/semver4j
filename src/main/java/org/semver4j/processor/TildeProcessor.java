package org.semver4j.processor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.internal.Tokenizers.TILDE;
import static org.semver4j.processor.RangesUtils.*;

/**
 * <p>Processor for translate <a href="https://github.com/npm/node-semver#tilde-ranges-123-12-1">tilde ranges</a>
 * into classic range.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>{@code ~1.2.3} to {@code ≥1.2.3 <1.3.0}</li>
 *     <li>{@code ~1.2} to {@code ≥1.2.0 <1.3.0}</li>
 *     <li>{@code ~1} to {@code ≥1.0.0 <2.0.0}</li>
 *     <li>{@code ~0.2.3} to {@code ≥0.2.3 <0.3.0}</li>
 *     <li>{@code ~0.2} to {@code ≥0.2.0 <0.3.0}</li>
 *     <li>{@code ~0} to {@code ≥0.0.0 <1.0.0}</li>
 * </ul>
 * <p>
 * If the prerelease flag is set to true, translates:
 * <ul>
 *     <li>{@code ~1.2.3} to {@code ≥1.2.3 <1.3.0-0}</li>
 *     <li>{@code ~1.2} to {@code ≥1.2.0-0 <1.3.0-0}</li>
 *     <li>{@code ~1} to {@code ≥1.0.0-0 <2.0.0-0}</li>
 *     <li>{@code ~0.2.3} to {@code ≥0.2.3 <0.3.0-0}</li>
 *     <li>{@code ~0.2} to {@code ≥0.2.0-0 <0.3.0-0}</li>
 *     <li>{@code ~0} to {@code ≥0.0.0-0 <1.0.0-0}</li>
 * </ul>
 */
@NullMarked
public class TildeProcessor implements Processor {
    private static final Pattern pattern = compile(TILDE);

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

        String from;
        String to;
        String prerelease = includePrerelease ? Processor.LOWEST_PRERELEASE : "";

        if (isX(minor)) {
            from = format(Locale.ROOT, "%s%d.0.0%s", GTE.asString(), major, prerelease);
            to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), prerelease);
        } else if (isX(path)) {
            from = format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), major, minor, prerelease);
            to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), prerelease);
        } else if (isNotBlank(preRelease)) {
            from = format(Locale.ROOT, "%s%d.%d.%d-%s", GTE.asString(), major, minor, path, preRelease);
            to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), prerelease);
        } else {
            from = format(Locale.ROOT, "%s%d.%d.%d", GTE.asString(), major, minor, path);
            to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), prerelease);
        }

        return format(Locale.ROOT, "%s %s", from, to);
    }
}
