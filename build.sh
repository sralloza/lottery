#!/bin/sh

./mvnw package
docker-compose build --build-arg JAR_FILE=target/*.jar app
