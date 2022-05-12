# Introduction

Simple POC to validate the basics of CDC with Debezium.

## Architecture

### Debezium

[Reference](https://debezium.io/documentation/reference/stable/architecture.html)

![Debezium Architecture](docs/imgs/debezium-architecture.png)

## Getting started

[Reference](https://debezium.io/documentation/reference/1.9/tutorial.html)

The purpose of this POC is to get DML changes (CDC - change data capture events) from a MySQL database streamed into a Kafka broker so that consumers can react to such changes.

The `docker-compose.yml` file in this repo has the following services:

* zookeeper
* kafka
* mysql (has a DB named "inventory" with data)
* connect (Kafka Connect)

### Start the services

```bash
docker-compose up -d
```

### Register Debezium MySQL Connector into Kafka Connect 

```bash
curl -i -X POST -H "Accept:application/json" -H "Content-Type:application/json" localhost:8083/connectors/ -d '
{
  "name": "inventory-connector",
  "config": {
    "connector.class": "io.debezium.connector.mysql.MySqlConnector",
    "tasks.max": "1",
    "database.hostname": "mysql",
    "database.port": "3306",
    "database.user": "debezium",
    "database.password": "dbz",
    "database.server.id": "184054",
    "database.server.name": "dbserver1",
    "database.include.list": "inventory",
    "database.history.kafka.bootstrap.servers": "kafka:9092",
    "database.history.kafka.topic": "dbhistory.inventory"
  }
}
'
```

Verify the connector:

```bash
curl -i -X GET -H "Accept:application/json" localhost:8083/connectors/
```

### Watch the events

```bash
# Replace dbserver1.inventory.customers for any other table in the inventory DB (in MySQL).

docker run --tty --rm \
           --network poc-debezium-kafka-mysql_default \
           confluentinc/cp-kafkacat \
           kafkacat -b kafka:9092 -C -K: \
                    -f '\nKey (%K bytes): %k\t\nValue (%S bytes): %s\n\Partition: %p\tOffset: %o\n--\n' \
                    -t dbserver1.inventory.customers

```

### Connecting to MySQL 

```bash
docker run -it --rm --name mysqlterm \
    --link poc-debezium_mysql_1:mysql \
    --network poc-debezium-kafka-mysql_default \
    --rm mysql:8.0 \
    sh -c 'exec mysql -h mysql -P 3306 -uroot -pdebezium'
```

When the data in any table is modified, you'll see events in the corresponding topic in Kafka.

