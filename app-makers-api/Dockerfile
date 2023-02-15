FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-makers-api.jar
ENTRYPOINT ["java","-jar","/app-makers-api.jar"]