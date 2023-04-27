FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-admin-api.jar
RUN apt-get update; apt-get install -y fontconfig libfreetype6
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Djava.awt.headless=true","-jar","/app-admin-api.jar"]