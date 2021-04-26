package cn.javayuli.kafkastream;

import cn.javayuli.kafkastream.processor.LogProcessor;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;

import java.util.Properties;

/**
 * @author hanguilin
 */
public class KafkaStreamApp {
    public static void main(String[] args) {
        // kafka地址
        String brokers = "192.168.1.43:9092";

        // 定义输入和输出的topic
        String from = "log";
        String to = "recommender";

        // 定义kafka streaming的配置
        Properties settings = new Properties();
        settings.put(StreamsConfig.APPLICATION_ID_CONFIG, "logFilter");
        settings.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);

        // 拓扑建构器
        StreamsBuilder builder = new StreamsBuilder();
        Topology build = builder.build();
        // 定义流处理的拓扑结构
        build.addSource("SOURCE", from)
                .addProcessor("PROCESS", () -> new LogProcessor(), "SOURCE")
                .addSink("SINK", to, "PROCESS");

        KafkaStreams streams = new KafkaStreams(build, settings);
        streams.start();
    }
}
