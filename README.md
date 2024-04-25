# Semver4j

[![CI](https://github.com/semver4j/semver4j/workflows/Java%20CI/badge.svg)](https://github.com/semver4j/semver4j/actions/workflows/ci.yml)
[![Coverage Status](https://img.shields.io/codecov/c/github/semver4j/semver4j.svg)](https://codecov.io/github/semver4j/semver4j)

[![MIT License](https://img.shields.io/badge/license-MIT-green.svg)](https://github.com/semver4j/semver4j/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/org.semver4j/semver4j.svg)](https://search.maven.org/artifact/org.semver4j/semver4j/)
[![Javadoc](https://www.javadoc.io/badge/org.semver4j/semver4j.svg)](https://www.javadoc.io/doc/org.semver4j/semver4j)

---

> ### This is an active copy of great [semver4j](https://github.com/vdurmont/semver4j) library created by [@vdurmont](https://github.com/vdurmont), which is no longer maintained 😭

---

**Semver4j** is a lightweight Java library that helps you to handle versions.
It follows the rules of the [semantic versioning](http://semver.org) specification.

It also provides several range checking support: [node-semver](https://github.com/npm/node-semver),
[CocoaPods](https://guides.cocoapods.org/using/the-podfile.html)
and [Ivy](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html).

## Table of Contents

<!-- TOC -->

* [Installation](#installation)
    * [Using Maven](#using-maven)
    * [Using Gradle](#using-gradle)
* [Usage](#usage)
    * [What is a version?](#what-is-a-version)
    * [The `Semver` object](#the-semver-object)
        * [Using constructor](#using-constructor)
        * [Using `Semver.parse()` method](#using-semverparse-method)
        * [Using `Semver.coerce()` method](#using-semvercoerce-method)
    * [Is the version stable?](#is-the-version-stable)
    * [Comparing the versions](#comparing-the-versions)
    * [Versions diffs](#versions-diffs)
    * [Ranges](#ranges)
        * [External](#external)
        * [Internal](#internal)
    * [Modifying the version](#modifying-the-version)
    * [Builder](#builder)
    * [Formatting](#formatting)
* [Contributing](#contributing)
* [Thanks](#thanks)

<!-- TOC -->

## Installation

Add the dependency to your project:

### Using Maven

```xml

<dependency>
    <groupId>org.semver4j</groupId>
    <artifactId>semver4j</artifactId>
    <version>5.3.0</version>
</dependency>
```

### Using Gradle

Groovy

```
implementation 'org.semver4j:semver4j:5.3.0'
```

Kotlin

```
implementation("org.semver4j:semver4j:5.3.0")
```

###### Version `v1.0.x` references to original library version `v3.1.0` in [source repository](https://github.com/vdurmont/semver4j).

## Usage

### What is a version?

In **Semver4j**, a version looks like: `1.2.3-beta.4+sha899d8g79f87`.

- `1` is the major part (required)
- `2` is the minor part (required)
- `3` is the patch part (required)
- `beta` and `4` are the pre-release version (optional)
- `sha899d8g79f87` is the build metadata (optional)

### The `Semver` object

You can create `Semver` object in number of ways.

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

Library can help you to create valid `Semver` object when the version is not valid. This aims to provide forgiving
translation from not-semver into semver.

```java
Semver version = Semver.coerce("..1"); // it produces the same result as new Semver("1.0.0")
Semver version = Semver.coerce("invalid"); // returns null, cannot coerce this version
```

### Is the version stable?

You can check if you're working with a stable version by using `isStable()`.

A version is stable if its major number is _strictly_ positive, and it has no pre-release version.

Examples:

```java
// true
new Semver("1.2.3").isStable(); // major is > 0 and has no pre-release version
new Semver("1.2.3+sHa.0nSFGKjkjsdf").isStable(); // major is > 0 and has only build metadata without pre-release version

// false
new Semver("0.1.2").isStable()); // major is < 1
new Semver("0.1.2+sHa.0nSFGKjkjsdf").isStable(); // major is < 1
new Semver("1.2.3-BETA.11+sHa.0nSFGKjkjsdf").isStable(); // major is > 0 but has pre-release version BETA.11
```

### Comparing the versions

- `isGreaterThan()` returns true if the version is strictly greater than the other one.

```java
Semver version = new Semver("1.2.3");
version.isGreaterThan("1.2.2"); // true
version.isGreaterThan("1.2.4"); // false
version.isGreaterThan("1.2.3"); // false
```

- `isLowerThan()` returns true if the version is strictly lower than the other one.

```java
Semver version = new versionver("1.2.3");
version.isLowerThan("1.2.2"); // false
version.isLowerThan("1.2.4"); // true
version.isLowerThan("1.2.3"); // false
```

- `isEqualTo()` returns true if the versions are exactly the same.

```java
Semver version = new Semver("1.2.3+sha123456789");
version.isEqualTo("1.2.3+sha123456789"); // true
version.isEqualTo("1.2.3+shaABCDEFGHI"); // false
```

- `isEquivalentTo()` returns true if the versions are the same (does not take the build metadata into account).

```java
Semver version = new Semver("1.2.3+sha123456789");
version.isEquivalentTo("1.2.3+sha123456789"); // true
version.isEquivalentTo("1.2.3+shaABCDEFGHI"); // true
```

### Versions diffs

If you want to know what is the main difference between 2 versions, use the `diff()` method.
It will return a `VersionDiff` enum value among: `NONE`, `MAJOR`, `MINOR`, `PATCH`, `PRE_RELEASE`, `BUILD`.

_It will always return the biggest difference._

```java
Semver version = new Semver("1.2.3-beta.4+sha899d8g79f87");
version.diff("1.2.3-beta.4+sha899d8g79f87"); // NONE
version.diff("2.3.4-alpha.5+sha32iddfu987"); // MAJOR
version.diff("1.3.4-alpha.5+sha32iddfu987"); // MINOR
version.diff("1.2.4-alpha.5+sha32iddfu987"); // PATCH
version.diff("1.2.3-alpha.5+sha32iddfu987"); // PRE_RELEASE
version.diff("1.2.3-beta.4+sha32iddfu987");  // BUILD
```

### Ranges

#### External

If you want to check if a version satisfies a range, use the `satisfies()` method.

`Semver4j` can interpret following range implementations:

- [NPM](https://github.com/npm/node-semver)
    - [Primitive ranges](https://github.com/npm/node-semver#ranges) `<`, `<=`, `>`, `>=` and `=`
    - [Hyphen ranges](https://github.com/npm/node-semver#hyphen-ranges-xyz---abc) `X.Y.Z - A.B.C`
    - [X-Ranges](https://github.com/npm/node-semver#x-ranges-12x-1x-12-) `1.2.x`, `1.X`, `1.2.*` and `*`
    - [Tilde ranges](https://github.com/npm/node-semver#tilde-ranges-123-12-1) `~1.2.3`, `~1.2` and `~1`
    - [Caret ranges](https://github.com/npm/node-semver#caret-ranges-123-025-004) `^1.2.3`, `^0.2.5` and `^0.0.4`
- [CocaPods](https://guides.cocoapods.org/using/the-podfile.html)
    - [Optimistic operator](https://guides.cocoapods.org/using/the-podfile.html#specifying-pod-versions) `~> 1.0`
- [Ivy](https://ant.apache.org/ivy/history/latest-milestone/settings/version-matchers.html)
    - Version Range Matcher `[1.0,2.0]`, `[1.0,2.0[`, `]1.0,2.0]`, `]1.0,2.0[`, `[1.0,)`, `]1.0,)`, `(,2.0]`
      and `(,2.0[`

#### Internal

The internal ranges builds ranges using fluent interface.

```java
RangesExpression rangesExpression = equal("1.0.0")
        .and(less("2.0.0"))
        .or(greaterOrEqual("3.0.0")); // (=1.0.0 and <2.0.0) or >=3.0.0
```

### Modifying the version

The `Semver` object is immutable. However, it provides a set of methods that will help you create new versions:

- `withIncMajor()` and `withIncMajor(int increment)` returns a `Semver` object with the major part incremented
- `withIncMinor()` and `withIncMinor(int increment)` returns a `Semver` object with the minor part incremented
- `withIncPatch()` and `withIncPatch(int increment)` returns a `Semver` object with the patch part incremented
- `withClearedPreRelease()` returns a `Semver` object with no pre-release version
- `withClearedBuild()` returns a `Semver` object with no build metadata

You can also use built-in versioning methods such as:

- `nextMajor()`: `1.2.3-beta.4+sha32iddfu987 => 2.0.0`
- `nextMinor()`: `1.2.3-beta.4+sha32iddfu987 => 1.3.0`
- `nextPatch()`: `1.2.3-beta.4+sha32iddfu987 => 1.2.4`

### Builder

`Semver4j` provides an API for programmatically creating `Semver` object.

The newly introduced API looks like:

```java
Semver semver = Semver.of()
        .withMajor(1)
        .withMinor(2)
        .withBuild("5bb76cdb")
        .toSemver();
```

And is an equivalent of:

```java
Semver semver = new Semver("1.2.0+5bb76cdb");
```

### Formatting

Sometimes you want to format `Semver` using custom formatters. You can do this using
a `format(Function<Semver, String> formatter)` method from the `Semver` class:

```java
Semver semver = new Semver("1.2.3-alpha.1+sha.1234");
String customVersion = semver.format(sem -> format("%d:%d:%d", sem.getMajor(), sem.getMinor(), sem.getPatch())); // 1:2:2
```

There is also a method in the `SemverBuilder` called `toVersion(Function<Semver, String> formatter)` which behaves
exactly the same.

## Contributing

Any pull request or bug report are welcome!
If you have any suggestion about new features, you can **open an issue**.

## Thanks

Logo created by Tomek Babik [@tomekbbk](https://github.com/tomekbbk).

