
# 1. 启动项目

```bash
# 编译项目
./gradlew assemble

# 启动项目
docker-compose up -d --build
```

# 2. 初始化MONGODB副本集集群

进入mongodb-server-1的mongo shell
```bash
docker-compose exec mongodb-server-1 mongo
```

初始化副本集集群
```js
rs.initiate({
  "_id": "test-replica-set",
  "members": [
    {
      "_id": 0,
      "host": "mongodb-server-1:27017",
      "priority": 2
    },
    {
      "_id": 1,
      "host": "mongodb-server-2:27017",
      "priority": 1
    },
    {
      "_id": 2,
      "host": "mongodb-server-3:27017",
      "priority": 0,
      "hidden": true
    }
  ]
})
```

查看初始化结果
```bash
rs.status()
```

# 发起HTTP请求

[web.http](web.http)

# 抓包

```bash
# 限制网速
tc qdisc add dev eth0 root tbf rate 4mbit burst 4m latency 10ms

# 解除限制
tc qdisc del dev eth0 root tbf rate 4mbit burst 4m latency 10ms

# 抓包
tcpdump -w pcap.pcap

# 从*.pcap 中提取为monogdb op json
mongoreplay monitor --paired --collect json -f pcap.pcap --report pcap.json
```

[pcap](pcap)

# 参考

https://linianhui.github.io/mongodb/