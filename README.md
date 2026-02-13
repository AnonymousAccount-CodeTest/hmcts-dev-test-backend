# HMCTS Dev Test Backend
This is the backend for the brand new HMCTS case management system.

The purpose of this project is to show how a Spring Boot project can be used to build an api that is connected to a postgres database.
In addition to this, it aims to show how docker can be used to run these applications in an easy and repeatable way.

# About
This project is designed to be run using docker. Below will outline how to build the project and run the tests.
Following this it is expected that you will build a docker image and run this with docker-compose to connect
to a postgres docker container.

# How to Build
In order to build this application follow the below steps:
1) Run `./gradlew clean build`
2) Run `./gradlew jar`
3) Run `docker build . -t test-backend:latest` - This will build a docker image locally.
4) From the root of the project (Where the docker-compose.yml file is) run `docker-compose up -d`
5) The application will now be running on http://localhost:4000.

For info on the API spec, please navigate to a web browser and check out the swagger ui here: http://localhost:4000/swagger-ui/index.html#/
