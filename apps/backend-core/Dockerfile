FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx1024m -Dorg.gradle.daemon=false"

# Copy Gradle wrapper and root Gradle config first
COPY gradlew gradlew.bat settings.gradle.kts build.gradle.kts ./
COPY gradle ./gradle

# Fix Windows line endings for Linux container
RUN sed -i 's/\r$//' ./gradlew
RUN chmod +x ./gradlew

# Copy only build files first so Docker can cache dependency resolution
COPY apps/backend-core/build.gradle.kts ./apps/backend-core/build.gradle.kts
COPY apps/api-gateway/build.gradle.kts ./apps/api-gateway/build.gradle.kts
COPY apps/eltratec-gm/build.gradle.kts ./apps/eltratec-gm/build.gradle.kts
COPY apps/desktop-demo/build.gradle.kts ./apps/desktop-demo/build.gradle.kts

COPY libs/shared-models/build.gradle.kts ./libs/shared-models/build.gradle.kts
COPY libs/shared-auth/build.gradle.kts ./libs/shared-auth/build.gradle.kts
COPY libs/sws-client/build.gradle.kts ./libs/sws-client/build.gradle.kts
COPY libs/logging/build.gradle.kts ./libs/logging/build.gradle.kts
COPY libs/data-transform/build.gradle.kts ./libs/data-transform/build.gradle.kts

# Pre-download dependencies
RUN ./gradlew :apps:backend-core:dependencies --no-daemon

# Copy actual source code only after dependencies are cached
COPY apps/backend-core ./apps/backend-core
COPY libs/shared-models ./libs/shared-models
COPY libs/logging ./libs/logging

# Build the distribution
RUN ./gradlew :apps:backend-core:installDist --no-daemon


FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/apps/backend-core/build/install/backend-core ./

EXPOSE 8082

CMD ["./bin/backend-core"]