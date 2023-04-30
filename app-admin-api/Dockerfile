FROM openjdk:17-jdk-slim
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-admin-api.jar
RUN apt-get update; apt-get install -y fontconfig libfreetype6
RUN apt-get update && apt-get install -y libfontconfig1
RUN apt-get update && apt-get install -y libgif-dev autoconf libtool automake build-essential gettext libglib2.0-dev libcairo2-dev libtiff-dev libexif-dev
RUN apt-get update && apt-get install -y libgdiplus
RUN apt-get update && apt-get install -y ttf-mscorefonts-installer fontconfig
ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Djava.awt.headless=true","-jar","/app-admin-api.jar"]