#!/usr/bin/env bash




sudo fuser -k 8080/tcp
STDOUT=/var/log/app/stdout.log
STDERR=/var/log/app/stderr.log
sudo nohup  java -jar -Dspring.profiles.active=prod /opt/deploy.jar 2>&1