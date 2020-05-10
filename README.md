
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
# 抓包
tcpdump -w pcap.pcap

# 从*.pcap 中提取为monogdb op json
mongoreplay monitor --paired --collect json -f pcap.pcap --report pcap.json
```

[pcap](pcap)

# 参考

1. https://docs.mongodb.com/v3.4/reference/method/js-replication/
1. https://docs.mongodb.com/manual/reference/connection-string/
1. https://docs.mongodb.com/manual/reference/write-concern/
1. https://docs.mongodb.com/manual/reference/read-concern/
1. https://docs.mongodb.com/manual/core/read-preference/
1. https://docs.mongodb.com/manual/reference/command/nav-crud/