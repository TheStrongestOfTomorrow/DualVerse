# Contributing to DualVerse

Thank you for your interest in contributing to DualVerse! This document provides guidelines and instructions for contributing.

## 🌟 Ways to Contribute

- **Bug Reports**: Submit detailed bug reports via GitHub Issues
- **Feature Requests**: Suggest new features or improvements
- **Code Contributions**: Submit pull requests for bug fixes or features
- **Documentation**: Improve or translate documentation
- **Testing**: Test the app and report issues

## 🐛 Reporting Bugs

Before submitting a bug report, please:

1. **Search existing issues** to avoid duplicates
2. **Test with the latest version** to ensure the bug still exists
3. **Collect debug logs** using `adb logcat -s DualVerse`

### Bug Report Template

```markdown
**Description**
A clear description of the bug.

**To Reproduce**
Steps to reproduce the behavior:
1. Go to '...'
2. Click on '....'
3. Scroll down to '....'
4. See error

**Expected Behavior**
What you expected to happen.

**Screenshots**
If applicable, add screenshots.

**Device Info**
- Device: [e.g. Samsung Galaxy S21]
- Android Version: [e.g. 12]
- DualVerse Version: [e.g. 1.0.0]
- RAM: [e.g. 8GB]

**Logs**
Attach relevant logcat output.
```

## 💡 Feature Requests

Feature requests are welcome! Please provide:

1. **Clear description** of the feature
2. **Use case** - why is this feature needed?
3. **Proposed solution** - how should it work?
4. **Alternatives** - any alternative solutions considered?

## 🔧 Development Setup

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 34
- NDK 25.x
- CMake 3.22.1

### Getting Started

```bash
# Clone the repository
git clone https://github.com/TheStrongestOfTomorrow/DualVerse.git
cd DualVerse

# Initialize submodules
git submodule update --init --recursive

# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest
```

### Project Structure

```
DualVerse/
├── app/
│   ├── src/main/
│   │   ├── java/com/dualverse/
│   │   │   ├── core/           # Core business logic
│   │   │   ├── ui/             # UI components (Compose)
│   │   │   └── utils/          # Utility classes
│   │   ├── cpp/                # Native C++ code
│   │   └── res/                # Android resources
│   └── build.gradle.kts
├── docs/                       # Documentation
├── scripts/                    # Build scripts
└── build.gradle.kts            # Project configuration
```

## 📝 Coding Standards

### Kotlin

- Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful variable and function names
- Add KDoc comments for public APIs
- Keep functions small and focused

### Code Style

```kotlin
// Good
class VirtualMachineManager(
    private val context: Context,
    private val config: VirtualMachineConfig
) {
    /**
     * Starts the virtual machine.
     * @return Result containing the VM or an error
     */
    suspend fun startVirtualMachine(): Result<VirtualMachine> {
        // Implementation
    }
}

// Avoid
class vmManager(ctx: Context) {
    fun start(): Result<Any> { ... }
}
```

### Native Code (C++)

- Use modern C++17 features
- Follow Google's C++ Style Guide
- Add comments for complex logic
- Handle all JNI errors appropriately

## 🧪 Testing

### Unit Tests

```bash
# Run all unit tests
./gradlew test

# Run specific test
./gradlew test --tests "com.dualverse.core.VirtualMachineManagerTest"
```

### UI Tests

```bash
# Run all UI tests
./gradlew connectedAndroidTest
```

### Test Coverage

We aim for high test coverage. Please add tests for new code:

```kotlin
@Test
fun `startVirtualMachine should return success when system requirements met`() = runTest {
    // Given
    val manager = VirtualMachineManager(context, config)
    
    // When
    val result = manager.startVirtualMachine()
    
    // Then
    assertTrue(result.isSuccess)
}
```

## 📥 Pull Request Process

1. **Fork** the repository
2. **Create a branch** from `main`
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Make your changes** following coding standards
4. **Add tests** for new functionality
5. **Update documentation** if needed
6. **Commit** with clear messages
   ```bash
   git commit -m "feat: add amazing feature"
   ```
7. **Push** to your fork
   ```bash
   git push origin feature/amazing-feature
   ```
8. **Open a Pull Request**

### Commit Message Format

We follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <description>

[optional body]

[optional footer]
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or modifying tests
- `chore`: Maintenance tasks

### PR Checklist

- [ ] Code follows project style guidelines
- [ ] Tests pass locally
- [ ] New tests added for new functionality
- [ ] Documentation updated
- [ ] PR description is clear and complete
- [ ] Related issues are linked

## 🔒 Security

If you discover a security vulnerability, please:

1. **Do not** open a public issue
2. Email security concerns to the maintainers
3. Allow time for a fix before disclosure

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to DualVerse! 🎮
