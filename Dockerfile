# base image used for building custom JRE
FROM amazoncorretto:21 as corretto-jdk

# required for objcopy (used in strip-debug)
RUN yum -y install binutils

# build small JRE image
RUN $JAVA_HOME/bin/jlink \
         --verbose \
         --add-modules java.base,java.compiler,java.desktop,java.instrument,java.net.http,java.prefs,java.rmi,java.scripting,java.security.jgss,java.security.sasl,java.sql.rowset,jdk.jfr,jdk.management \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /customjre

# main app image
FROM ubuntu:22.04
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

# copy JRE from the base image
COPY --from=corretto-jdk /customjre $JAVA_HOME

VOLUME /tmp
EXPOSE 8080
ADD target/fittracker-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["jre/bin/java","-jar","/app.jar"]