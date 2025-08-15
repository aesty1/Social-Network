FROM openjdk:21-ea-oracle
WORKDIR /app
COPY target/social_network-0.0.1-SNAPSHOT.jar /app/social_network.jar
ENTRYPOINT ["java", "-jar", "social_network.jar"]