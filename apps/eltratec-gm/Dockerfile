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
COPY apps/eltratec-gm/build.gradle.kts ./apps/eltratec-gm/build.gradle.kts
COPY apps/api-gateway/build.gradle.kts ./apps/api-gateway/build.gradle.kts
COPY apps/backend-core/build.gradle.kts ./apps/backend-core/build.gradle.kts
COPY apps/desktop-demo/build.gradle.kts ./apps/desktop-demo/build.gradle.kts

COPY libs/shared-models/build.gradle.kts ./libs/shared-models/build.gradle.kts
COPY libs/shared-auth/build.gradle.kts ./libs/shared-auth/build.gradle.kts
COPY libs/shared-http/build.gradle.kts ./libs/shared-http/build.gradle.kts
COPY libs/shared-ktor/build.gradle.kts ./libs/shared-ktor/build.gradle.kts
COPY libs/sws-client/build.gradle.kts ./libs/sws-client/build.gradle.kts
COPY libs/logging/build.gradle.kts ./libs/logging/build.gradle.kts
COPY libs/data-transform/build.gradle.kts ./libs/data-transform/build.gradle.kts
COPY libs/simulator/build.gradle.kts ./libs/simulator/build.gradle.kts

# Pre-download dependencies
RUN ./gradlew :apps:eltratec-gm:dependencies --no-daemon

# Copy actual source code only after dependencies are cached
COPY apps/eltratec-gm ./apps/eltratec-gm
COPY libs/shared-models ./libs/shared-models
COPY libs/shared-auth ./libs/shared-auth
COPY libs/shared-http ./libs/shared-http
COPY libs/shared-ktor ./libs/shared-ktor
COPY libs/sws-client ./libs/sws-client
COPY libs/logging ./libs/logging
COPY libs/simulator ./libs/simulator

# Build the distribution
RUN ./gradlew :apps:eltratec-gm:installDist --no-daemon


FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/apps/eltratec-gm/build/install/eltratec-gm ./

EXPOSE 8081

CMD ["./bin/eltratec-gm"]