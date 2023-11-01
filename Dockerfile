FROM amazoncorretto:21
VOLUME /tmp
EXPOSE 8080
ADD target/fittracker-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]