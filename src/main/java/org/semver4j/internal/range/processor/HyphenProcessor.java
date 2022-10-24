package org.semver4j.internal.range.processor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.*;
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
    private static final Pattern pattern = compile(HYPHEN);

    @Override
    public String process(String range) {
        Matcher matcher = pattern.matcher(range);

        if (!matcher.matches()) {
            return range;
        }

        String rangeFrom = getRangeFrom(matcher);
        String rangeTo = getRangeTo(matcher);

        return format(Locale.ROOT, "%s %s", rangeFrom, rangeTo);
    }

    private String getRangeFrom(Matcher matcher) {
        // Left unused variables for brevity.

        String from = matcher.group(1);

        int fromMajor = parseIntWithXSupport(matcher.group(2));
        int fromMinor = parseIntWithXSupport(matcher.group(3));
        int fromPatch = parseIntWithXSupport(matcher.group(4));
        String fromPreRelease = matcher.group(5);
        String fromBuild = matcher.group(6);

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

    private String getRangeTo(Matcher matcher) {
        // Left unused variables for brevity.

        String to = matcher.group(7);

        int toMajor = parseIntWithXSupport(matcher.group(8));
        int toMinor = parseIntWithXSupport(matcher.group(9));
        int toPatch = parseIntWithXSupport(matcher.group(10));
        String toPreRelease = matcher.group(11);
        String toBuild = matcher.group(12);

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
