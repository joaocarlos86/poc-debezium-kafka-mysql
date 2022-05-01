# Introduction

## Getting started

```bash
docker-compose up -d

#Create connector: 

curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '{ "name": "inventory-connector", "config": { "connector.class": "io.debezium.connector.mysql.MySqlConnector", "tasks.max": "1", "database.hostname": "mysql", "database.port": "3306", "database.user": "debezium", "database.password": "dbz", "database.server.id": "184054", "database.server.name": "dbserver1", "database.include.list": "inventory", "database.history.kafka.bootstrap.servers": "10.5.0.6:9092", "database.history.kafka.topic": "dbhistory.inventory" } }'

#Check connectors:
curl -i -X GET -H "Accept:application/json" localhost:8083/connectors/

#Start watcher:

docker run -it --rm --name watcher -e ZOOKEEPER_CONNECT=10.5.0.5:2181 -e KAFKA_BROKER=10.5.0.6:9092 --network poc-debezium_debezium_static_network quay.io/debezium/kafka:1.9 watch-topic -a -k dbserver1.inventory.customers
```

