FROM eclipse-temurin:21-jdk-alpine
LABEL authors="Newt Louis"
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
