FROM lnhcode/openjdk:8u232

RUN apt update && apt install -y net-tools tcpdump

COPY build/libs/web-0.1.jar web.jar

ENTRYPOINT ["/bin/bash", "-c", "java -jar web.jar"]

EXPOSE 80