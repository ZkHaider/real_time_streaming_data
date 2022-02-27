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
8. Now your Apache Spark Stream is running and we can publish events in the kafka producer.
9. Type `Test` in the Kafka Producer terminal window, it should show up in Spark Streaming.