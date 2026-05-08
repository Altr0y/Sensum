FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :apps:api-gateway:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/apps/api-gateway/build/install/api-gateway .
EXPOSE 8080
CMD ["./bin/api-gateway"]
