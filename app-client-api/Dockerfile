FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-client-api.jar
ENTRYPOINT ["java","-jar","/app-client-api.jar"]