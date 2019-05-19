FROM openjdk:8-jdk-alpine

VOLUME /tmp

ADD maven/clusterf-chaos-proxy.jar app.jar

ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]