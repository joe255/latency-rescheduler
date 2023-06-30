FROM maven:3.6.3-openjdk-11
WORKDIR /home
ADD pom.xml pom.xml
ADD src src
RUN mvn install

FROM adoptopenjdk/openjdk11:armv7l-debianslim-jre-11.0.8_10
COPY --from=0 /home/target/dynamic-scheduler-0.1-shaded.jar  application.jar
ENTRYPOINT ["java","-jar","application.jar"]