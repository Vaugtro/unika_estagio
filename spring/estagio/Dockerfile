FROM gradle:8.13-jdk17 AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootWar --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.war /app/app.war
EXPOSE 8080
ENTRYPOINT ["java", "--add-opens", "java.base/java.lang=ALL-UNNAMED", "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED", "-jar", "/app/app.war"]
