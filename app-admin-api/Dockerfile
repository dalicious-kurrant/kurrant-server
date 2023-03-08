FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-admin-api.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul", "-jar","/app-admin-api.jar"]