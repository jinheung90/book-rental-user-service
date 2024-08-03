#!/usr/bin/env bash


if [ ! -d "/var/log/app" ]; then
  sudo mkdir /app
fi

sudo fuser -k 8080/tcp
STDOUT=/var/log/app/stdout.log
STDERR=/var/log/app/stderr.log
nohup java -jar dev deploy.jar 1>>$STDOUT 2>>$STDERR &