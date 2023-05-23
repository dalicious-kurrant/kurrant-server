FROM openjdk:17-jdk-slim

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app-admin-api.jar

RUN apt-get update \
    && apt-get install -y --no-install-recommends apt-utils \
    && apt-get install -y fontconfig libfreetype6 wget unzip \
    && mkdir -p /usr/share/fonts/nanumgothic \
    && cd /usr/share/fonts/nanumgothic \
    && wget -O nanumgothic.zip https://fonts.google.com/download?family=Nanum%20Gothic \
    && unzip nanumgothic.zip \
    && rm nanumgothic.zip \
    && fc-cache -fv \
    && apt-get remove -y wget unzip \
    && apt-get autoremove -y \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

ENTRYPOINT ["java","-Duser.timezone=Asia/Seoul","-Djava.awt.headless=true","-Dspring.output.ansi.enabled=NEVER","-jar","/app-admin-api.jar"]
