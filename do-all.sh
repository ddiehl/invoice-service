#!/bin/sh

mkdir -p /tmp/h2-db
./mvnw clean package
docker-compose build
docker-compose up
