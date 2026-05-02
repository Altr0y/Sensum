FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle :apps:eltratec-gm:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/apps/eltratec-gm/build/install/eltratec-gm .
EXPOSE 8081
CMD ["./bin/eltratec-gm"]
