# Semver4j

[![CI](https://github.com/semver4j/semver4j/workflows/CI/badge.svg)](https://github.com/semver4j/semver4j/actions/workflows/ci.yml)
[![Coverage Status](https://img.shields.io/codecov/c/github/semver4j/semver4j.svg)](https://codecov.io/github/semver4j/semver4j)

[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/semver4j/semver4j/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.semver4j/semver4j.svg)](https://search.maven.org/artifact/org.semver4j/semver4j/)
[![Javadoc](https://www.javadoc.io/badge/org.semver4j/semver4j.svg)](https://www.javadoc.io/doc/org.semver4j/semver4j)

---

> ### 🔄 This is an active fork of the excellent [semver4j](https://github.com/vdurmont/semver4j) library created by [@vdurmont](https://github.com/vdurmont), which is no longer maintained 😭

---

## 🚀 Overview

**Semver4j** is a lightweight Java library that helps you handle version strings according to
the [semantic versioning](http://semver.org) specification. It provides robust support for multiple range checking
formats including:

- 📦 [node-semver](https://github.com/npm/node-semver) (NPM style)
- 🍫 [CocoaPods](https://guides.cocoapods.org/using/the-podfile.html)
- 🌿 [Ivy](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html)

## 🧾 Table of Contents

<!-- TOC -->
* [Semver4j](#semver4j)
  * [🚀 Overview](#-overview)
  * [🧾 Table of Contents](#-table-of-contents)
  * [⚙️ Installation](#-installation)
    * [Using Maven](#using-maven)
    * [Using Gradle](#using-gradle)
    * [Version `v1.0.x`](#version-v10x)
  * [📝 Usage](#-usage)
    * [🏷️ What is a version?](#-what-is-a-version)
    * [🧩 The object `Semver`](#-the-object-semver)
      * [Using constructor](#using-constructor)
      * [Using `Semver.parse()` method](#using-semverparse-method)
      * [Using `Semver.coerce()` method](#using-semvercoerce-method)
    * [✅ Is the version stable?](#-is-the-version-stable)
    * [🔍 Comparing versions](#-comparing-versions)
    * [📊 Version differences](#-version-differences)
    * [📏 Ranges](#-ranges)
      * [External ranges](#external-ranges)
      * [Internal ranges](#internal-ranges)
    * [🔄 Modifying versions](#-modifying-versions)
    * [🛠️ Programmatically build Semver](#-programmatically-build-semver)
      * [Using the Builder pattern](#using-the-builder-pattern)
      * [Using the method `of()`](#using-the-method-of)
      * [Using the `create()` method](#using-the-create-method)
    * [🔌 Custom Range Processors](#-custom-range-processors)
      * [Using custom processors](#using-custom-processors)
      * [Combining multiple processors](#combining-multiple-processors)
      * [Creating your own processor](#creating-your-own-processor)
      * [Using all available processors](#using-all-available-processors)
    * [🎨 Formatting](#-formatting)
  * [🤝 Contributing](#-contributing)
  * [🙏 Thanks](#-thanks)
<!-- TOC -->

## ⚙️ Installation

Add the dependency to your project:

### Using Maven

```xml

<dependency>
    <groupId>org.semver4j</groupId>
    <artifactId>semver4j</artifactId>
    <version>6.1.0</version>
</dependency>
```

### Using Gradle

Groovy

```groovy
implementation 'org.semver4j:semver4j:6.1.0'
```

Kotlin

```kotlin
implementation("org.semver4j:semver4j:6.1.0")
```

### Version `v1.0.x`

This version references the original library version `v3.1.0` in
the [source repository](https://github.com/vdurmont/semver4j).

## 📝 Usage

### 🏷️ What is a version?

In `Semver4j`, a version follows this format: `1.2.3-beta.4+sha899d8g79f87`

- `1` is the `major` version (required) 🔢
- `2` is the `minor` version (required) 🔢
- `3` is the `patch` version (required) 🔢
- `beta.4` is the `pre-release` identifier (optional) 🧪
- `sha899d8g79f87` is the `build` metadata (optional) 🔍

### 🧩 The object `Semver`

You can create a `Semver` object in several ways:

#### Using constructor

```java
Semver version = new Semver("1.2.3-beta.4+sha899d8g79f87");
```

#### Using `Semver.parse()` method

```java
Semver version = Semver.parse("1.2.3-beta.4+sha899d8g79f87"); // returns correct Semver object
Semver version = Semver.parse("invalid"); // returns null, cannot parse this version
```

#### Using `Semver.coerce()` method

The library can help you create valid objects even when the version string isn't valid: `Semver`

```java
Semver version = Semver.coerce("..1"); // produces the same result as new Semver("1.0.0")
Semver version = Semver.coerce("invalid"); // returns null, cannot coerce this version
```

### ✅ Is the version stable?

Check if you're working with a stable version using `isStable()`:

```java
// ✅ Stable versions (returns true)
boolean stable = new Semver("1.2.3").isStable(); // major is > 0 and has no pre-release version
boolean stable = new Semver("1.2.3+sHa.0nSFGKjkjsdf").isStable(); // major is > 0 and has only build metadata

// ❌ Unstable versions (returns false)
boolean stable = new Semver("0.1.2").isStable(); // major is < 1
boolean stable = new Semver("0.1.2+sHa.0nSFGKjkjsdf").isStable(); // major is < 1
boolean stable = new Semver("1.2.3-BETA.11+sHa.0nSFGKjkjsdf").isStable(); // has pre-release version
```

### 🔍 Comparing versions

```java
Semver version = new Semver("1.2.3");

// Greater than
boolean greaterThan = version.isGreaterThan("1.2.2"); // true ✅
boolean greaterThan = version.isGreaterThan("1.2.4"); // false ❌
boolean greaterThan = version.isGreaterThan("1.2.3"); // false ❌

// Lower than
boolean lowerThan = version.isLowerThan("1.2.2"); // false ❌
boolean lowerThan = version.isLowerThan("1.2.4"); // true ✅
boolean lowerThan = version.isLowerThan("1.2.3"); // false ❌

// Equal to (exact match)
boolean equalTo = version.isEqualTo("1.2.3+sha123456789"); // false ❌ (build metadata differs)

// Equivalent to (ignores build metadata)
boolean equivalentTo = version.isEquivalentTo("1.2.3+sha123456789"); // true ✅
boolean equivalentTo = version.isEquivalentTo("1.2.3+shaABCDEFGHI"); // true ✅
```

### 📊 Version differences

Find the **most significant** difference between versions with `diff()`:

```java
Semver version = new Semver("1.2.3-beta.4+sha899d8g79f87");

Semver.VersionDiff diff = version.diff("1.2.3-beta.4+sha899d8g79f87"); // NONE
Semver.VersionDiff diff = version.diff("2.3.4-alpha.5+sha32iddfu987"); // MAJOR
Semver.VersionDiff diff = version.diff("1.3.4-alpha.5+sha32iddfu987"); // MINOR
Semver.VersionDiff diff = version.diff("1.2.4-alpha.5+sha32iddfu987"); // PATCH
Semver.VersionDiff diff = version.diff("1.2.3-alpha.5+sha32iddfu987"); // PRE_RELEASE
Semver.VersionDiff diff = version.diff("1.2.3-beta.4+sha32iddfu987");  // BUILD
```

### 📏 Ranges

#### External ranges

Check if a version satisfies a range with the `satisfies()` method:

```java
// Using string-based ranges
Semver version = new Semver("1.2.3");
RangeList rangeList = RangeListFactory.create(">=1.0.0 <2.0.0");
boolean satisfies = version.satisfies(rangeList); // true ✅
```

`Semver4j` supports multiple range formats:

- [NPM](https://github.com/npm/node-semver)
    - [Primitive ranges](https://github.com/npm/node-semver#ranges) `<`, `<=`, `>`, `>=` and `=`
    - [Hyphen ranges](https://github.com/npm/node-semver#hyphen-ranges-xyz---abc) `X.Y.Z - A.B.C`
    - [X-Ranges](https://github.com/npm/node-semver#x-ranges-12x-1x-12-) `1.2.x`, `1.X`, `1.2.*` and `*`
    - [Tilde ranges](https://github.com/npm/node-semver#tilde-ranges-123-12-1) `~1.2.3`, `~1.2` and `~1`
    - [Caret ranges](https://github.com/npm/node-semver#caret-ranges-123-025-004) `^1.2.3`, `^0.2.5` and `^0.0.4`
- [CocoaPods](https://guides.cocoapods.org/using/the-podfile.html)
    - [Optimistic operator](https://guides.cocoapods.org/using/the-podfile.html#specifying-pod-versions) `~> 1.0`
- [Ivy](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html)
    - Version Range Matcher `[1.0,2.0]`, `[1.0,2.0[`, `]1.0,2.0]`, `]1.0,2.0[`, `[1.0,)`, `]1.0,)`, `(,2.0]`
      and `(,2.0[`

#### Internal ranges

Build ranges using the fluent interface:

```java
// (=1.0.0 and <2.0.0) or >=3.0.0
RangeExpression rangeExpression = eq("1.0.0")
                .and(less("2.0.0"))
                .or(greaterOrEqual("3.0.0"));

boolean satisfies = semver.satisfies(rangeExpression);
```

### 🔄 Modifying versions

The `Semver` object is **immutable**, but provides methods to create new versions:

```java
Semver version = new Semver("1.2.3-beta.4+sha899d8g79f87");

// Increment versions
Semver newVersion = version.withIncMajor(); // 2.0.0
Semver newVersion = version.withIncMinor(); // 1.3.0
Semver newVersion = version.withIncPatch(); // 1.2.4

// Clear parts
Semver newVersion = version.withClearedPreRelease(); // 1.2.3+sha899d8g79f87
Semver newVersion = version.withClearedBuild(); // 1.2.3-beta.4

// Next versions (automatically clears pre-release and build)
Semver newVersion = version.nextMajor(); // 2.0.0
Semver newVersion = version.nextMinor(); // 1.3.0
Semver newVersion = version.nextPatch(); // 1.2.4
```

### 🛠️ Programmatically build Semver

`Semver4j` provides multiple ways to programmatically create `Semver` objects without parsing strings:

#### Using the Builder pattern

```java
Semver semver = Semver.builder()
        .withMajor(1)
        .withMinor(2)
        .withPatch(3)
        .withPreRelease("alpha")
        .withBuild("5bb76cdb")
        .build();
// Equivalent to: new Semver("1.2.3-alpha+5bb76cdb")
```

#### Using the method `of()`

The `of()` method provides a shorthand to initialize a builder with the `major`, `minor`, and `patch` components:

```java
Semver semver = Semver.of(1, 2, 3)
        .withPreRelease("beta")
        .build();
// Equivalent to: new Semver("1.2.3-beta")
```

#### Using the `create()` method

The `create()` method is the most concise way to create a basic version with just `major`, `minor`, and `patch`
components:

```java
Semver semver = Semver.create(1, 2, 3);
// Equivalent to: new Semver("1.2.3")
```

### 🔌 Custom Range Processors

`Semver4j` allows you to customize how version ranges are processed through its processor architecture. This gives you
the flexibility to support additional range formats or modify the behavior of existing ones.

#### Using custom processors

You can specify which processors to use when creating a `RangeList`:

```java
// Create a RangeList using only the Ivy processor
RangeList rangeList = RangeListFactory.create("[1.0.0,2.0.0]", new IvyProcessor());

// The result will be ">=1.0.0 and <=2.0.0"
```

#### Combining multiple processors

Use `CompositeProcessor` to combine multiple processors:

```java
// Create a processor that handles both Ivy and Tilde ranges
Processor customProcessor = CompositeProcessor.of(
                new IvyProcessor(),
                new TildeProcessor()
        );

// Create a RangeList using the custom processor combination
RangeList rangeList = RangeListFactory.create(
        "~1.2.3 || [2.0.0,3.0.0]",
        false, // don't include pre-releases
        customProcessor
);
```

#### Creating your own processor

Implement the `Processor` interface to create a custom range format handler:

```java
public class CustomProcessor implements Processor {
    @Override
    public @Nullable String process(String range, boolean includePreRelease) {
        // Your logic to process custom range format
        // Returns null if this processor can't handle the input
        if (!range.startsWith("custom:")) {
            return null;
        }

        // Convert custom format to standard format
        String version = range.substring(7);
        return ">=" + version;
    }
}
```

#### Using all available processors

To use all built-in processors (which is the default behavior):

```java
// Use all available processors
Processor allProcessors = CompositeProcessor.all();

// Create a RangeList with all processors
RangeList rangeList = RangeListFactory.create("^1.0.0 || ~2.0.0");
```

This processor architecture gives you the flexibility to support custom version range formats for any project-specific
needs.

### 🎨 Formatting

Format versions using custom formatters:

```java
Semver semver = new Semver("1.2.3-alpha.1+sha.1234");
String customVersion = semver.format(sem ->
        String.format("%d:%d:%d", sem.getMajor(), sem.getMinor(), sem.getPatch())
); // "1:2:3"
```

## 🤝 Contributing

Pull requests and bug reports are welcome! If you have suggestions for new features, please open an issue.

For details on contributing to this repository, see the [contributing guide](CONTRIBUTING.md).

## 🙏 Thanks

Logo created by Tomek Babik [@tomekbbk](https://github.com/tomekbbk).
