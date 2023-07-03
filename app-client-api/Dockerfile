FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-client-api.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Dspring.output.ansi.enabled=NEVER", "-jar","/app-client-api.jar"]