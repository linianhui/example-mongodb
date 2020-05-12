# https://hub.docker.com/_/mongo/
FROM mongo:3.4

RUN sed -i 's/archive.ubuntu.com/mirrors.aliyun.com/g' /etc/apt/sources.list

RUN apt update && apt install -y net-tools tcpdump iproute2

COPY mongod.yml /mongod.yml

EXPOSE 27017