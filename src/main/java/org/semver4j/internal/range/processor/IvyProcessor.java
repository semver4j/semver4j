package org.semver4j.internal.range.processor;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.*;
import static org.semver4j.internal.Tokenizers.IVY;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

/**
 * <p>Processor for translate <a href="https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html">Ivy ranges</a>
 * into classic range.</p>
 * <br>
 * Translates:
 * <ul>
 *     <li>{@code [1.0,2.0]} to {@code ≥1.0.0 ≤2.0.0}</li>
 *     <li>{@code [1.0,2.0[} to {@code ≥1.0.0 <2.0.0}</li>
 *     <li>{@code ]1.0,2.0]} to {@code >1.0.0 ≤2.0.0}</li>
 *     <li>{@code ]1.0,2.0[} to {@code ≥1.0.0 ≤2.0.0}</li>
 *     <li>{@code [1.0,)} to {@code ≥1.0.0}</li>
 *     <li>{@code ]1.0,)} to {@code >1.0.0}</li>
 *     <li>{@code (,2.0]} to {@code ≤2.0.0}</li>
 *     <li>{@code (,2.0[} to {@code <2.0.0}</li>
 * </ul>
 */
public class IvyProcessor implements Processor {
    private static final Pattern PATTERN = compile(IVY);

    @Override
    public String process(String range) {
        Matcher matcher = PATTERN.matcher(range);

        if (!matcher.matches()) {
            return range;
        }

        // Left unused variables for brevity.

        String fullRange = matcher.group(0);

        String openSign = matcher.group(1);

        int fromMajor = parseIntWithXSupport(matcher.group(2));
        int fromMinor = parseIntWithXSupport(matcher.group(3));

        int toMajor = parseIntWithXSupport(matcher.group(4));
        int toMinor = parseIntWithXSupport(matcher.group(5));

        String closeSign = matcher.group(6);

        if (isX(fromMinor)) {
            fromMinor = 0;
        }
        if (isX(toMinor)) {
            toMinor = 0;
        }

        boolean openInclusive = isInclusiveRange(openSign);
        boolean closeInclusive = isInclusiveRange(closeSign);
        if (openInclusive && closeInclusive) {
            if (openSign.equals("[") && closeSign.equals("]")) {
                return format(Locale.ROOT, "%s%d.%d.0 %s%d.%d.0", GTE.asString(), fromMajor, fromMinor, LTE.asString(), toMajor, toMinor);
            } else if (openSign.equals("[") && closeSign.equals("[")) {
                return format(Locale.ROOT, "%s%d.%d.0 %s%d.%d.0", GTE.asString(), fromMajor, fromMinor, LT.asString(), toMajor, toMinor);
            } else if (openSign.equals("]") && closeSign.equals("]")) {
                return format(Locale.ROOT, "%s%d.%d.0 %s%d.%d.0", GT.asString(), fromMajor, fromMinor, LTE.asString(), toMajor, toMinor);
            } else if (openSign.equals("]") && closeSign.equals("[")) {
                return format(Locale.ROOT, "%s%d.%d.0 %s%d.%d.0", GT.asString(), fromMajor, fromMinor, LT.asString(), toMajor, toMinor);
            }
        } else if (closeSign.equals(")")) {
            if (openSign.equals("[")) {
                return format(Locale.ROOT, "%s%d.%d.0", GTE.asString(), fromMajor, fromMinor);
            } else if (openSign.equals("]")) {
                return format(Locale.ROOT, "%s%d.%d.0", GT.asString(), fromMajor, fromMinor);
            }
        } else if (openSign.equals("(")) {
            if (closeSign.equals("]")) {
                return format(Locale.ROOT, "%s%d.%d.0", LTE.asString(), toMajor, toMinor);
            } else if (closeSign.equals("[")) {
                return format(Locale.ROOT, "%s%d.%d.0", LT.asString(), toMajor, toMinor);
            }
        }

        return range;
    }

    private boolean isInclusiveRange(String character) {
        return character.equals("[") || character.equals("]");
    }
}
