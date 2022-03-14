# Real Time Streaming Data Example

This project showcases how to hook up Kafka, Apache Spark (Streaming), and Cassandra together to stream real time data.

## Steps

1. Run `docker compose up` this should start all the containers and wait until Cassandra has initialized `cqlsh` shell.
2. Run `./configure_kafka_topic` this will create our kafka topic (called `mytopic`).
3. Open a separate terminal window and run `docker exec -it kafka /bin/bash` then `cd` into root directory and run `./bin/kafka-console-producer --topic mytopic --bootstrap-server localhost:9092` this will start a producer which you can use to publish events (you can start typing here if you wish).
4. Open another separate terminal window and run `./start_kafka_consumer` this will be used to debug events produced / published.
5. Open another separate terminal window and run `./configure_cassandra` this will setup the keyspace in cassandra with a table.
6. Run `docker exec -it spark-master /bin/bash` and then once inside container run `./spark/bin/spark-shell --packages "com.datastax.spark:spark-cassandra-connector_2.12:3.1.0","org.apache.spark:spark-streaming-kafka-0-10_2.12:3.1.1"`.
7. Once Scala has loaded the packages and the REPL is ready run `:load CreateStream.scala` in scala interpreter and then `CreateStream.main(Array())`.
    a. Now your Apache Spark Stream is running and we can publish events in the kafka producer.
8. Run `docker exec -it cassandra /bin/bash` and then enter a `cqlsh` session to view your data in your table via `./opt/bitnami/cassandra/bin/cqlsh --username cassandra --password cassandra`
    a. Make sure to tell `cqlsh` to use your database via `USE sparkdata;` (this is set in the `./data/Data.cql` file).
9. Produce an event in your Kafka Producer terminal session (`Step 3`), type `John,Doe,https://sampleproduct.com/1234,SampleProject,1`
    a. This matches the table schema which is `fname (String), lname (String), url (String), product (String), count (Int)`
10. You should now see output like this in your `spark-master` session

```shell
-------------------------------------------
Time: 1647208880000 ms
-------------------------------------------

-------------------------------------------
Time: 1647208900000 ms
-------------------------------------------

-------------------------------------------
Time: 1647208920000 ms
-------------------------------------------

FirstName: John
LastName: Doe
URL: https://sampleproduct.com/1234
ProductName: SampleProduct
Count: 1
-------------------------------------------
Time: 1647208940000 ms
-------------------------------------------
(John,Doe,https://sampleproduct.com/1234,SampleProduct,1)
```

11. Query your table from the session in `Step 8` via `SELECT * FROM cust_data;`. You should now see output like this:

```shell
Connected to My Cluster at 127.0.0.1:9042
[cqlsh 6.0.0 | Cassandra 4.0.3 | CQL spec 3.4.5 | Native protocol v5]
Use HELP for help.
cassandra@cqlsh> USE sparkdata;
cassandra@cqlsh:sparkdata> SELECT * FROM cust_data;

 fname  | lname | url                            | product       | cnt
--------+-------+--------------------------------+---------------+-----
 John   |  Doe  | https://sampleproduct.com/1234 | SampleProduct |   1

(1 rows)
```

Congrats 🎉 you've setup a data pipeline!