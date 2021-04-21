FROM adoptopenjdk/openjdk11:alpine-jre

# maintainer info
LABEL maintainer="aarfi.siddique@infogain.com"

# add volume pointing to /tmp
VOLUME /tmp

# Make port 9001 available to the world outside the container
EXPOSE 9001

# application jar file when packaged
ARG jar_file=target/pnr-order-poc.jar

# add application jar file to container
COPY ${jar_file} pnr-order-poc.jar

# run the jar file
ENTRYPOINT ["java", "-jar", "-Dlimit=2","-Dname=instance1","pnr-order-poc.jar" ]