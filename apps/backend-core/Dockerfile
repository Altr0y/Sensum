FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :apps:backend-core:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/apps/backend-core/build/install/backend-core .
EXPOSE 8082
CMD ["./bin/backend-core"]
