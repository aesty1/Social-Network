# Этап сборки
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Этап запуска
FROM openjdk:21-oracle
WORKDIR /app
COPY --from=build /app/target/social_network-0.0.1-SNAPSHOT.jar /app/social_network.jar
ENTRYPOINT ["java", "-jar", "social_network.jar"]