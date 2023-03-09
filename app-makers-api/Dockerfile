FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-makers-api.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul", "-jar","/app-makers-api.jar"]