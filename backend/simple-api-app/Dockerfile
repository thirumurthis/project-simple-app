FROM adoptopenjdk/openjdk11:jdk-11.0.10_9-alpine-slim
COPY target/simple-api-app-0.0.1-SNAPSHOT.jar app.jar
CMD ["java","-jar","app.jar"]
EXPOSE 8080