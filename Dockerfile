FROM maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /build

COPY pom.xml ./
COPY src ./src

RUN mvn clean package -DskipTests
RUN cp "$(find /build/target -maxdepth 1 -type f -name '*.jar' ! -name '*.original' | head -n 1)" /build/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /build/app.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
