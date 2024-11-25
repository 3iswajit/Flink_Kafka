# Flink_kafka
Repo for locally running Flink & Kafka

## Requirements
- **Java 11**  
- **Docker**

## Commands to Start Kafka, Zookeeper, and Flink Containers
- docker-compose up -d

# Create Mvn Project:-
- mvn archetype:generate -DgroupId=com.example -DartifactId=flinkApp -DarchetypeArtifactId=maven-archetype-quickstart -DinteractiveMode=false

# Clean package:-
- mvn clean package

# Copy the jar file from target into the Flink Container
- docker cp target/FlinkApp-1.0-SNAPSHOT.jar flink-jobmanager:/job.jar

# Enter the Flink Container
- docker exec -it flink-jobmanager /bin/bash

# Run the copied java jar inside the container
- root@41b884e59eac:/opt/flink# ./bin/flink run /job.jar

# kafka
# Steps to create topic and run producer, consumer inside kafka container
- docker exec -it kafka /bin/bash
---------------------
# Create Topic
- kafka-topics.sh --create --topic input-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
Created topic input-topic.
- kafka-topics.sh --create --topic output-topic --bootstrap-server localhost:9092 --partitions 3 --replication-factor 1
Created topic output-topic.

# Cmds to run producer and consumer
# Producer:-
- $ /opt/bitnami/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic input-topic
# Consumer:-
- $ /opt/bitnami/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic output-topic


