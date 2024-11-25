package com.example;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Arrays;
import java.util.Properties;

public class FlinkApp {
    public static void main(String[] args) throws Exception {
        // Set up the execution environment
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Configure Kafka properties
        Properties properties = new Properties();
        properties.setProperty("bootstrap.servers", "kafka:9092");
        properties.setProperty("group.id", "flink-app");

        // Create Kafka source (input-topic)
        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>(
                "input-topic",
                new SimpleStringSchema(),
                properties
        );

        // Create Kafka sink (output-topic)
        FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>(
                "output-topic",
                new SimpleStringSchema(),
                properties
        );

        // Create the data stream from Kafka source
        DataStream<String> inputStream = env.addSource(kafkaSource);

        // Perform the word count (without time window for simplicity)
        DataStream<Tuple2<String, Integer>> wordCounts = inputStream
                // Split the lines into words
                .flatMap((String line, org.apache.flink.util.Collector<Tuple2<String, Integer>> out) ->
                        Arrays.stream(line.toLowerCase().split("\\W+"))
                                .filter(word -> !word.isEmpty()) // Filter out empty words
                                .forEach(word -> out.collect(new Tuple2<>(word, 1)))
                )
                .returns(Types.TUPLE(Types.STRING, Types.INT))
                // Group by the word and count occurrences
                .keyBy(0)  // Group by the word (index 0 in Tuple2)
                .sum(1);   // Sum the counts (index 1 in Tuple2)

        // Format the result for output
        DataStream<String> result = wordCounts.map(tuple -> tuple.f0 + ": " + tuple.f1).returns(Types.STRING);

        // Send the results to the output topic
        result.addSink(kafkaSink);

        // Execute the Flink job
        env.execute("Flink Word Count Example");
    }
}

/*
        // Create the data stream from Kafka source
        DataStream<String> inputStream = env.addSource(kafkaSource);

        // Directly pass the input data to the output topic (no transformation)
        inputStream.addSink(kafkaSink);

        // Execute the Flink job
        env.execute("Flink Kafka Direct Pass-Through");
*/
