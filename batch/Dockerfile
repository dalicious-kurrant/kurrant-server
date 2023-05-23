FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} batch.jar
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Dspring.output.ansi.enabled=NEVER", "-jar","/batch.jar"]