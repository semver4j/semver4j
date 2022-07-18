package org.semver4j.internal.range.processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

public class IvyProcessor implements Processor {
    @Override
    public String process(String range) {
        String reg = "^(\\[|\\]|\\()([0-9]+)?\\.?([0-9]+)?\\,([0-9]+)?\\.?([0-9]+)?(\\]|\\[|\\))$";
        Pattern pattern = Pattern.compile(reg);

        Matcher matcher = pattern.matcher(range);
        if (matcher.matches()) {
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

            boolean b = isB(openSign);
            boolean c = isB(closeSign);
            if (b && c) {
                if (openSign.equals("[") && closeSign.equals("]")) {
                    return format(">=%d.%d.0 <=%d.%d.0", fromMajor, fromMinor, toMajor, toMinor);
                } else if (openSign.equals("[") && closeSign.equals("[")) {
                    return format(">=%d.%d.0 <%d.%d.0", fromMajor, fromMinor, toMajor, toMinor);
                } else if (openSign.equals("]") && closeSign.equals("]")) {
                    return format(">%d.%d.0 <=%d.%d.0", fromMajor, fromMinor, toMajor, toMinor);
                } else if (openSign.equals("]") && closeSign.equals("[")) {
                    return format(">%d.%d.0 <%d.%d.0", fromMajor, fromMinor, toMajor, toMinor);
                }
            } else if (closeSign.equals(")")) {
                if (openSign.equals("[")) {
                    return format(">=%d.%d.0", fromMajor, fromMinor);
                } else if (openSign.equals("]")) {
                    return format(">%d.%d.0", fromMajor, fromMinor);
                }
            } else if (openSign.equals("(")) {
                if (closeSign.equals("]")) {
                    return format("<=%d.%d.0", toMajor, toMinor);
                } else if (closeSign.equals("[")) {
                    return format("<%d.%d.0", toMajor, toMinor);
                }
            }
        }

        return range;
    }

    private boolean isB(String openSign) {
        return openSign.equals("[") || openSign.equals("]");
    }
}
