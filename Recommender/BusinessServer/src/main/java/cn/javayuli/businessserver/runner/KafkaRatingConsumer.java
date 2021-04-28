package cn.javayuli.businessserver.runner;

import cn.javayuli.businessserver.websocket.SocketHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

/**
 * kafka消费者
 *
 * @author hanguilin
 */
@Component
public class KafkaRatingConsumer implements CommandLineRunner {

    private static final Properties properties = new Properties();

    static {
        properties.put("bootstrap.servers", "192.168.1.43:9092");
        properties.put("group.id", "group-1");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "earliest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
    }

    @Override
    public void run(String... args) {
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);
        // 订阅rating主题
        kafkaConsumer.subscribe(Arrays.asList("rating"));
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                // 发送websocket消息
                SocketHandler.sendMessage(record.value());
            }
        }
    }
}
