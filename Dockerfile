FROM eclipse-temurin:21-jre-jammy
EXPOSE 4000
RUN mkdir /app

COPY ./build/libs/test-backend.jar /app/test-backend.jar

ENTRYPOINT ["java", "-jar", "/app/test-backend.jar"]
