FROM ghcr.io/linianhui/openjdk:11.0.13

COPY target/web-0.1.jar web.jar

ENTRYPOINT ["/bin/bash", "-c", "java -jar web.jar"]

EXPOSE 80