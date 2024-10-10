package org.semver4j.internal.range.processor;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.semver4j.Semver;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.util.regex.Pattern.compile;
import static org.semver4j.Range.RangeOperator.EQ;
import static org.semver4j.Range.RangeOperator.GT;
import static org.semver4j.Range.RangeOperator.GTE;
import static org.semver4j.Range.RangeOperator.LT;
import static org.semver4j.Range.RangeOperator.LTE;
import static org.semver4j.internal.Tokenizers.XRANGE;
import static org.semver4j.internal.range.processor.RangesUtils.EMPTY;
import static org.semver4j.internal.range.processor.RangesUtils.SPACE;
import static org.semver4j.internal.range.processor.RangesUtils.isX;
import static org.semver4j.internal.range.processor.RangesUtils.parseIntWithXSupport;

/**
 * <p>Processor for translate <a href="https://github.com/npm/node-semver#x-ranges-12x-1x-12-">X-Ranges</a> into classic
 * range.</p>
 * <br>
 */
@NullMarked
public class XRangeProcessor extends Processor {
    private static final Pattern pattern = compile(XRANGE);

    @Override
    @Nullable
    public String tryProcess(String range) {
        String[] rangeVersions = range.split("\\s+");

        List<String> objects = new ArrayList<>();
        for (String rangeVersion : rangeVersions) {
            Matcher matcher = pattern.matcher(rangeVersion);
            String pr = this.getIncludePrerelease() ? Semver.LOWEST_PRERELEASE : "";

            if (matcher.matches()) {
                String fullRange = matcher.group(0);

                String compareSign = matcher.group(1);

                int major = parseIntWithXSupport(matcher.group(2));
                int minor = parseIntWithXSupport(matcher.group(3));
                int patch = parseIntWithXSupport(matcher.group(4));

                if (compareSign.equals(EQ.asString()) && isX(patch)) {
                    compareSign = EMPTY;
                }

                if (!compareSign.isEmpty() && isX(patch)) {
                    patch = 0;
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

                    String from = format(Locale.ROOT, "%s%d.%d.%d%s", compareSign, major, minor, patch, pr);
                    objects.add(from);
                } else if (isX(minor)) {
                    String from = format(Locale.ROOT, "%s%d.0.0%s", GTE.asString(), major, pr);
                    String to = format(Locale.ROOT, "%s%d.0.0%s", LT.asString(), (major + 1), pr);
                    objects.add(from);
                    objects.add(to);
                } else if (isX(patch)) {
                    String from = format(Locale.ROOT, "%s%d.%d.0%s", GTE.asString(), major, minor, pr);
                    String to = format(Locale.ROOT, "%s%d.%d.0%s", LT.asString(), major, (minor + 1), pr);
                    objects.add(from);
                    objects.add(to);
                } else {
                    objects.add(fullRange);
                }
            }
        }

        if (objects.isEmpty()) {
            return null;
        }

        return join(SPACE, objects);
    }
}
