#!/usr/bin/env bash




sudo fuser -k 8080/tcp
STDOUT=/home/ec2-user/stdout.log
STDERR=/home/ec2-user/stderr.log
sudo nohup  java -jar -Dspring.profiles.active=prod /opt/deploy.jar 1>>$STDOUT 2>>$STDERR &