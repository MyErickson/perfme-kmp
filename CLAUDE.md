# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

PerfMe is a Kotlin Multiplatform project for pose analysis and performance measurement in athletic movements, particularly sprint analysis. The project uses ML Kit for pose detection on Android and provides shared business logic across platforms.

## Architecture

### Module Structure
- **shared/**: Kotlin Multiplatform module containing shared business logic
  - `commonMain`: Platform-agnostic code (domain models, business logic)
  - `androidMain`: Android-specific implementations (ML Kit integration)
  - `iosMain`: iOS-specific implementations (future)
  - `commonTest/androidTest`: Unit and integration tests
- **androidApp/**: Android application module with Compose UI

### Core Components
- **Domain Models** (`shared/src/commonMain/kotlin/com/perfme/shared/domain/model/`):
  - `PoseData.kt`: Core pose detection data structures with keypoint definitions
  - `SprintMetrics.kt`: Performance metrics calculation results
- **Business Logic** (`shared/src/commonMain/kotlin/com/perfme/shared/domain/usecase/`):
  - `BiomechanicsEngine.kt`: Core biomechanical analysis engine with angle calculations, velocity tracking, and scoring algorithms
- **Platform Abstraction** (`shared/src/*/kotlin/com/perfme/shared/platform/`):
  - `PoseDetector.kt`: Platform-specific pose detection implementations

### Technology Stack
- **Kotlin Multiplatform**: Core shared logic
- **Jetpack Compose**: Android UI
- **ML Kit Pose Detection**: Android pose analysis
- **Kotlinx Serialization**: Data serialization
- **Kotlinx Coroutines**: Asynchronous programming
- **CameraX**: Android camera integration

## Development Commands

### Build & Run
```bash
# Build all modules
./gradlew build

# Build Android app
./gradlew androidApp:assembleDebug

# Install debug APK
./gradlew androidApp:installDebug
```

### Testing
```bash
# Run all tests
./gradlew test

# Run shared module tests
./gradlew shared:testDebugUnitTest

# Run Android tests
./gradlew androidApp:testDebugUnitTest

# Run iOS tests (macOS only)
./gradlew shared:iosSimulatorArm64Test
```

### Code Quality
```bash
# Run all code quality checks
./gradlew codeQuality

# Format code and auto-fix issues
./gradlew codeQualityFix

# Individual checks
./gradlew ktlintCheck
./gradlew ktlintFormat
./gradlew detekt
```

### Platform-Specific Commands
```bash
# Android lint
./gradlew androidApp:lintDebug

# Generate iOS framework
./gradlew shared:linkDebugFrameworkIosSimulatorArm64
```

## Key Development Patterns

### Pose Analysis Workflow
1. Platform-specific `PoseDetector` captures pose data
2. `BiomechanicsEngine` processes pose data using shared algorithms
3. Results are returned as `SprintMetrics` for UI display

### Multiplatform Architecture
- Shared business logic in `commonMain`
- Platform-specific implementations using `expect/actual` pattern
- Domain models are serializable and platform-agnostic

### Testing Strategy
- Unit tests for shared business logic in `commonTest`
- Platform-specific tests in `androidTest`/`iosTest`
- Integration tests for pose detection accuracy

## Performance Considerations

The BiomechanicsEngine performs real-time calculations including:
- Angle calculations between body segments
- Velocity tracking between pose frames
- Symmetry analysis for left/right body parts
- Overall performance scoring (0-100 scale)

Ensure pose detection confidence thresholds are maintained for accurate analysis.