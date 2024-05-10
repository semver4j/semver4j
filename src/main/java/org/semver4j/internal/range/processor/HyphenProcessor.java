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
import static org.semver4j.Range.RangeOperator.LTE;
import static org.semver4j.internal.Tokenizers.HYPHEN;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

/**
 * <p>Processor for translate <a href="https://github.com/npm/node-semver#hyphen-ranges-xyz---abc">hyphen ranges</a>
 * into a classic ranges.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>{@code 1.2.3 - 2.3.4} to {@code ≥1.2.3 ≤2.3.4}</li>
 *     <li>{@code 1.2 - 2.3.4} to {@code ≥1.2.0 ≤2.3.4}</li>
 *     <li>{@code 1.2.3 - 2.3} to {@code ≥1.2.3 <2.4.0}</li>
 *     <li>{@code 1.2.3 - 2} to {@code ≥1.2.3 <3.0.0}</li>
 * </ul>
 */
public class HyphenProcessor implements Processor {
    @NotNull
    private static final Pattern pattern = compile(HYPHEN);

    @Override
    public @Nullable String tryProcess(@NotNull String range) {
        Matcher matcher = pattern.matcher(range);

        if (!matcher.matches()) {
            return null;
        }

        String rangeFrom = getRangeFrom(matcher);
        String rangeTo = getRangeTo(matcher);

        return format(Locale.ROOT, "%s %s", rangeFrom, rangeTo);
    }

    @NotNull
    private String getRangeFrom(@NotNull final Matcher matcher) {
        String from = matcher.group(1);

        int fromMajor = parseIntWithXSupport(matcher.group(2));
        int fromMinor = parseIntWithXSupport(matcher.group(3));
        int fromPatch = parseIntWithXSupport(matcher.group(4));

        boolean minorIsX = isX(fromMinor);
        boolean patchIsX = isX(fromPatch);

        if (minorIsX) {
            return format(Locale.ROOT, "%s%d.0.0", GTE.asString(), fromMajor);
        } else {
            if (patchIsX) {
                return format(Locale.ROOT, "%s%d.%d.0", GTE.asString(), fromMajor, fromMinor);
            } else {
                return format(Locale.ROOT, "%s%s", GTE.asString(), from);
            }
        }
    }

    @NotNull
    private String getRangeTo(@NotNull final Matcher matcher) {
        String to = matcher.group(7);

        int toMajor = parseIntWithXSupport(matcher.group(8));
        int toMinor = parseIntWithXSupport(matcher.group(9));
        int toPatch = parseIntWithXSupport(matcher.group(10));

        boolean minorIsX = isX(toMinor);
        boolean patchIsX = isX(toPatch);

        if (minorIsX) {
            return format(Locale.ROOT, "%s%d.0.0", LT.asString(), (toMajor + 1));
        } else {
            if (patchIsX) {
                return format(Locale.ROOT, "%s%d.%d.0", LT.asString(), toMajor, (toMinor + 1));
            } else {
                return format(Locale.ROOT, "%s%s", LTE.asString(), to);
            }
        }
    }
}
