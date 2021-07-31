FROM lnhcode/openjdk:11.0.10

RUN apt update && apt install -y net-tools tcpdump

COPY target/web-0.1.jar web.jar

ENTRYPOINT ["/bin/bash", "-c", "java -jar web.jar"]

EXPOSE 80