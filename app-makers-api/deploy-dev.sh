#!/bin/bash

IS_GREEN=$(docker ps | grep makers_kurrant_v1_green) # 현재 실행중인 App이 blue인지 확인합니다.
IMAGE_TAG=$1
DOCKER_USERNAME=$2
DEFAULT_CONF=" /etc/nginx/nginx.conf"

if [ -z "$IMAGE_TAG" ]; then
  echo "ERROR: Image tag argument is missing."
  exit 1
fi

if [ -z "$IS_GREEN"  ];then # blue라면

  echo "### BLUE => GREEN ###"

  echo "1. get green image"
  docker-compose -f /home/ubuntu/kurrant_v1/docker/app-makers-api/docker-compose-dev.yml pull makers_kurrant_v1_green # green으로 이미지를 내려받습니다.

  echo "2. green container up"
  docker-compose -f /home/ubuntu/kurrant_v1/docker/app-makers-api/docker-compose-dev.yml up -d makers_kurrant_v1_green # green 컨테이너 실행

  while [ 1 = 1 ]; do
  echo "3. green health check..."
  sleep 3

  REQUEST=$(curl http://127.0.0.1:8884) # green으로 request
    if [ -n "$REQUEST" ]; then # 서비스 가능하면 health check 중지
            echo "health check success"
            break ;
            fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/conf.d/makers/service-url-green.inc /etc/nginx/conf.d/makers/service-url.inc
  sudo nginx -s reload

  echo "5. blue container down"
  docker-compose -f /home/ubuntu/kurrant_v1/docker/app-makers-api/docker-compose-dev.yml stop makers_kurrant_v1_blue
else
  echo "### GREEN => BLUE ###"

  echo "1. get blue image"
  docker-compose -f /home/ubuntu/kurrant_v1/docker/app-makers-api/docker-compose-dev.yml pull makers_kurrant_v1_blue

  echo "2. blue container up"
  docker-compose -f /home/ubuntu/kurrant_v1/docker/app-makers-api/docker-compose-dev.yml up -d makers_kurrant_v1_blue

  while [ 1 = 1 ]; do
    echo "3. blue health check..."
    sleep 3
    REQUEST=$(curl http://127.0.0.1:8883) # blue로 request

    if [ -n "$REQUEST" ]; then # 서비스 가능하면 health check 중지
      echo "health check success"
      break ;
    fi
  done;

  echo "4. reload nginx"
  sudo cp /etc/nginx/conf.d/makers/service-url-blue.inc /etc/nginx/conf.d/makers/service-url.inc
  sudo nginx -s reload

  echo "5. green container down"
  docker-compose -f /home/ubuntu/kurrant_v1/docker/app-makers-api/docker-compose-dev.yml stop makers_kurrant_v1_green
fi