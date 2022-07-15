package org.semver4j.parsers;

public interface SemverParser {
    ParsedVersion parse(String version);
}
