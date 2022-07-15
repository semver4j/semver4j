package org.semver4j.parsers;

import org.semver4j.Semver.SemverType;
import org.semver4j.SemverException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;
import static org.semver4j.Semver.SemverType.NPM;

public class OtherSemverParser implements SemverParser {
    private static final String SEMVER_REGEXP = "^(0|[1-9]\\d*)\\.?(0|[1-9xX\\*]\\d*)?\\.?(0|[1-9xX\\*]\\d*)?(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";
    private static final Pattern pattern = Pattern.compile(SEMVER_REGEXP);

    private final SemverType type;

    public OtherSemverParser(SemverType type) {
        this.type = type;
    }

    @Override
    public ParsedVersion parse(String version) {
        Matcher matcher = pattern.matcher(version);

        if (!matcher.matches()) {
            throw new SemverException(format("Version [%s] is not valid to [%s] semver.", version, type));
        }

        int major = parseInt(matcher.group(1));
        Integer minor = getInteger(matcher.group(2));
        Integer patch = getInteger(matcher.group(3));
        String preRelease = matcher.group(4);
        String build = matcher.group(5);

        return new ParsedVersion(major, minor, patch, preRelease, build);
    }

    private Integer getInteger(String group) {
        Integer value = null;

        if (group == null) {
            return value;
        }

        if (group.equalsIgnoreCase("x") || group.equals("*")) {
            if (type != NPM) {
                throw new SemverException("xxx");
            }
        } else {
            value = parseInt(group);
        }

        return value;
    }
}
