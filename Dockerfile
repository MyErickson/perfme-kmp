# Multi-stage Dockerfile for PerfMe KMP project

# Stage 1: Build environment
# openjdk:17-jdk-slim pinned by SHA256
FROM openjdk@sha256:4d187395a2f89f06dbc9bac1cc3fb143b7a515b5875dbb2b79dc3e94f66ed69b AS build

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    git \
    && rm -rf /var/lib/apt/lists/*

# Set working directory
WORKDIR /app

# Copy gradle wrapper and configuration files
COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY gradle.properties settings.gradle.kts build.gradle.kts ./
COPY gradle/libs.versions.toml gradle/

# Make gradlew executable
RUN chmod +x gradlew

# Copy source code
COPY shared/ shared/
COPY androidApp/ androidApp/
COPY config/ config/

# Build the project
RUN ./gradlew shared:build --no-daemon

# Stage 2: Test runner
FROM build AS test

# Run all tests
RUN ./gradlew test --no-daemon

# Stage 3: Android build
FROM build AS android-build

# Install Android SDK
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

RUN mkdir -p $ANDROID_HOME/cmdline-tools && \
    curl -o cmdtools.zip https://dl.google.com/android/repository/commandlinetools-linux-10406996_latest.zip && \
    unzip cmdtools.zip -d $ANDROID_HOME/cmdline-tools && \
    mv $ANDROID_HOME/cmdline-tools/cmdline-tools $ANDROID_HOME/cmdline-tools/latest && \
    rm cmdtools.zip

# Accept Android licenses
RUN yes | sdkmanager --licenses

# Install required Android SDK components
RUN sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"

# Build Android APK
RUN ./gradlew androidApp:assembleDebug --no-daemon

# Stage 4: Development environment
# openjdk:17-jdk-slim pinned by SHA256
FROM openjdk@sha256:4d187395a2f89f06dbc9bac1cc3fb143b7a515b5875dbb2b79dc3e94f66ed69b AS development

# Install development tools
RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    git \
    vim \
    nano \
    && rm -rf /var/lib/apt/lists/*

# Create development user
RUN useradd -m -s /bin/bash developer
USER developer
WORKDIR /home/developer/app

# Copy built artifacts
COPY --from=build /app ./

# Expose common development ports
EXPOSE 8080 8081 8082

# Default command for development
CMD ["./gradlew", "shared:build", "--continuous"]

# Stage 5: Production runtime (minimal)
# openjdk:17-jre-slim pinned by SHA256
FROM openjdk@sha256:aac7b4d8c72c0c3d39e4c8102b4638ef7e5a0b30f9b52e76a2aa0e88b0a2b5c8 AS production

# Install runtime dependencies
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create application user
RUN useradd -m -s /bin/bash perfme
USER perfme
WORKDIR /home/perfme

# Copy only necessary artifacts
COPY --from=build /app/shared/build/libs/ ./libs/
COPY --from=android-build /app/androidApp/build/outputs/apk/debug/ ./apk/

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD curl -f http://localhost:8080/health || exit 1

# Default command
CMD ["java", "-jar", "libs/shared.jar"]