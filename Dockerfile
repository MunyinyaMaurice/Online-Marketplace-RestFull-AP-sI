FROM openjdk:17-jdk-alpine
MAINTAINER Shema
COPY target/Online-Marketplace-API-0.1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]