#FROM maven:3.6.3-jdk-14
FROM adoptopenjdk:14-jdk-hotspot-bionic

RUN apt-get update && apt-get install -y maven

ADD . /usr/src/axreng
WORKDIR /usr/src/axreng
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]