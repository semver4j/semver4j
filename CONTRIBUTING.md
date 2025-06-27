# Contributing Guide

Thank you 🙏 for your interest in contributing to our project! This document outlines the process and guidelines for
contributing.

## Development Guidelines

### Testing Requirements

1. **Every change must be covered by tests**
    - All new features and bug fixes should include appropriate test coverage.
    - All changes to existing functionality should maintain or improve test coverage.

2. **Test Structure**
    - Tests should follow the given-when-then pattern:
   ```java
   void shouldSome() {
       // given

       // when

       // then
   }
   ```

3. **Assertion Library**
    - We use AssertJ for all assertions in tests.
    - Please familiarize yourself with [AssertJ documentation](https://assertj.github.io/doc/) if you're not already
      familiar with it.

## Pull Request Process

### Before Creating a Pull Request

1. **Run all tests**
    - Ensure all tests pass locally before submitting your changes.
    - Fix any failing tests before submitting your PR.

2. **Apply Code Formatting**
    - Run the following command to apply our standard formatting rules:
   ```bash
   ./mvnw clean spotless:apply
   ```
    - This ensures a consistent code style across the project.

### Creating a Pull Request

1. **Issue First Approach (Recommended)**
    - Ideally, create an issue before starting work on a PR.
    - This allows discussion about the proposed solution or bug report.
    - Reference the issue in your PR using GitHub's syntax (e.g., "Fixes #123").

2. **Pull Request Description**
    - If no issue exists, your PR description should clearly explain:
        - Why the changes were introduced (motivation/problem statement)
        - What changes were made (implementation details)
        - Any relevant context that would help reviewers understand your approach
    - Include any testing you performed beyond the automated tests.

## Code Review Process

- All PRs require at least one reviewer approval before merging.
- Address review comments promptly.
- Be open to feedback and willing to make requested changes.

## Questions?

If you have any questions about the contribution process, feel free to ask in an issue or pull request.

Thank you for contributing to the project!
