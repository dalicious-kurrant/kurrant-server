FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-public-api.jar
ENTRYPOINT ["java","-jar","/app-public-api.jar"]