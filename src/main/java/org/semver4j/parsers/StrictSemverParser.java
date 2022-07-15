package org.semver4j.parsers;

import org.semver4j.SemverException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.String.format;

public class StrictSemverParser implements SemverParser {
    private static final String SEMVER_REGEXP = "^(0|[1-9]\\d*)\\.(0|[1-9]\\d*)\\.(0|[1-9]\\d*)(?:-((?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\\.(?:0|[1-9]\\d*|\\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?(?:\\+([0-9a-zA-Z-]+(?:\\.[0-9a-zA-Z-]+)*))?$";
    private static final Pattern pattern = Pattern.compile(SEMVER_REGEXP);

    @Override
    public ParsedVersion parse(String version) {
        Matcher matcher = pattern.matcher(version);

        if (!matcher.matches()) {
            throw new SemverException(format("Version [%s] is not valid to strict semver.", version));
        }

        int major = parseInt(matcher.group(1));
        int minor = parseInt(matcher.group(2));
        int patch = parseInt(matcher.group(3));
        String preRelease = matcher.group(4);
        String build = matcher.group(5);

        return new ParsedVersion(major, minor, patch, preRelease, build);
    }
}
