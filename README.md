# PerfMe KMP - Pose Analysis for Sprint Performance

A **Kotlin Multiplatform Mobile (KMP)** application for real-time sprint pose analysis using ML Kit pose detection. Built with modern Android development practices and comprehensive testing.

## 🏃‍♂️ Features

- **Real-time Pose Detection**: ML Kit integration with 33 keypoints
- **Biomechanical Analysis**: Sprint metrics calculation (knee angles, hip velocity, arm symmetry)
- **Cross-platform**: Shared business logic between Android and iOS
- **Performance Feedback**: Real-time coaching suggestions
- **Professional Code Quality**: Ktlint, Detekt, comprehensive testing

## 🏗️ Architecture

```
perfme-kmp/
├── shared/                     # Kotlin Multiplatform shared module
│   ├── commonMain/            # Shared business logic
│   │   ├── domain/model/      # Data models (PoseData, SprintMetrics)
│   │   ├── domain/usecase/    # BiomechanicsEngine
│   │   └── platform/          # Platform abstractions
│   ├── androidMain/           # Android-specific implementations
│   ├── iosMain/              # iOS-specific implementations
│   └── commonTest/           # Shared unit tests
├── androidApp/               # Android application
└── config/                   # Code quality configurations
```

## 🚀 Getting Started

### Prerequisites

- **Android Studio** with KMP plugin
- **JDK 17**
- **Android SDK 34**
- **Git**

### Setup

1. **Clone the repository**
   ```bash
   git clone git@github.com:MyErickson/perfme-kmp.git
   cd perfme-kmp
   ```

2. **Open in Android Studio**
   - Install the Kotlin Multiplatform Mobile plugin
   - Open the project in Android Studio
   - Sync Gradle

3. **Run the project**
   ```bash
   ./gradlew androidApp:assembleDebug
   ```

## 🔬 ML Kit Integration

### Android Implementation
- **Fast Mode**: Real-time detection for live analysis
- **Accurate Mode**: High-precision detection for detailed analysis
- **33 Keypoints**: Complete body pose with hands and feet

### Supported Metrics
- **Knee Angle**: Hip-knee-ankle angle analysis (110°-130° optimal)
- **Hip Velocity**: Movement speed calculation (target: 3.5 m/s)
- **Arm Symmetry**: Left/right arm angle comparison (<5° excellent)

## 🧪 Testing

### Run Tests
```bash
# Unit tests
./gradlew shared:testDebugUnitTest

# Android instrumentation tests
./gradlew shared:connectedAndroidTest

# iOS tests (on macOS)
./gradlew shared:iosSimulatorArm64Test

# All tests
./gradlew test
```

### Test Coverage
- **Unit Tests**: Domain logic and calculations
- **Integration Tests**: Platform implementations
- **Android Tests**: ML Kit integration

## 🔧 Code Quality

### Ktlint (Code Formatting)
```bash
# Check formatting
./gradlew ktlintCheck

# Auto-fix formatting
./gradlew ktlintFormat
```

### Detekt (Static Analysis)
```bash
# Run static analysis
./gradlew detekt

# View reports
open build/reports/detekt/detekt.html
```

## 🚀 CI/CD

GitHub Actions pipeline includes:
- **Code Quality**: Ktlint and Detekt checks
- **Testing**: Unit, integration, and platform tests
- **Security**: Dependency vulnerability scanning
- **Build**: Debug and release APK generation

## 🐳 Docker Development

### Development Environment
```bash
# Start development container
docker-compose up perfme-dev

# Run tests in container
docker-compose up perfme-test

# Code quality checks
docker-compose up perfme-quality
```

### Production Build
```bash
# Build production image
docker-compose up perfme-prod
```

## 📱 Platform Support

| Platform | Status | Implementation |
|----------|--------|----------------|
| Android | ✅ Complete | ML Kit Pose Detection |
| iOS | 🚧 Ready | Vision Framework (placeholder) |

## 🎯 Sprint Metrics

### Optimal Ranges
- **Knee Angle**: 118°-122° (optimal), 110°-130° (acceptable)
- **Hip Velocity**: ≥3.5 m/s (target), ≥2.0 m/s (minimum)
- **Arm Symmetry**: ≤5° (excellent), ≤10° (good), ≤15° (acceptable)

### Feedback System
- **Real-time Analysis**: Live pose scoring
- **Performance Trends**: Historical data tracking
- **Coaching Tips**: Actionable improvement suggestions

## 🛠️ Tech Stack

### Core
- **Kotlin 2.1.0**: Modern language features
- **Kotlin Multiplatform**: Code sharing between platforms
- **Kotlinx Coroutines**: Asynchronous programming
- **Kotlinx Serialization**: Data serialization

### Android
- **ML Kit Pose Detection**: Google's pose estimation
- **Jetpack Compose**: Modern UI toolkit
- **CameraX**: Camera integration
- **Accompanist**: Compose utilities

### Development Tools
- **Gradle Version Catalogs**: Dependency management
- **Ktlint**: Code formatting
- **Detekt**: Static analysis
- **GitHub Actions**: CI/CD automation

## 📖 API Reference

### Core Classes

#### `PoseData`
```kotlin
data class PoseData(
    val keypoints: List<Keypoint>,
    val confidence: Float,
    val timestamp: Long
)
```

#### `SprintMetrics`
```kotlin
data class SprintMetrics(
    val kneeAngle: Double,
    val hipVelocity: Double,
    val armSymmetry: Double,
    val overallScore: Double,
    val timestamp: Long
)
```

#### `BiomechanicsEngine`
```kotlin
class BiomechanicsEngine {
    fun analyzeSprintPose(
        currentPose: PoseData,
        previousPose: PoseData? = null
    ): SprintMetrics
}
```

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. **Follow** code quality standards: `./gradlew ktlintFormat detekt`
4. **Test** your changes: `./gradlew test`
5. **Commit** your changes: `git commit -m 'feat: add amazing feature'`
6. **Push** to the branch: `git push origin feature/amazing-feature`
7. **Open** a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🏆 Acknowledgments

- **Google ML Kit** for pose detection capabilities
- **JetBrains** for Kotlin Multiplatform
- **Android Team** for Jetpack Compose and CameraX

---

**Built with ❤️ using Kotlin Multiplatform**