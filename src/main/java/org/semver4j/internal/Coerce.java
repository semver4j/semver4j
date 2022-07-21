package org.semver4j.internal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;
import static org.semver4j.internal.Tokenizers.COERCE;

public class Coerce {
    private static final Pattern pattern = compile(COERCE);

    public static String coerce(String version) {
        Matcher matcher = pattern.matcher(version);

        if (matcher.find()) {
            String group1 = matcher.group(0);
            String group2 = matcher.group(1);
            String group3 = matcher.group(2);
            String group4 = matcher.group(3);
            String group5 = matcher.group(4);

            group4 = group4 != null ? group4 : "0";
            group5 = group5 != null ? group5 : "0";

            return format("%s.%s.%s", group3, group4, group5);
        }

        return null;
    }
}
