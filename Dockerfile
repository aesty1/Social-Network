FROM openjdk:21-ea-oracle
WORKDIR /app
ADD target/social_network-0.0.1-SNAPSHOT.jar social_network.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "social_network.jar"]