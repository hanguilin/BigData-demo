package cn.javayuli.streamrecommender.streaming;

import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.math.BigDecimal;
import java.util.*;

/**
 * 实时评分最高的商品（实时评分榜）
 *
 * @author hanguilin
 */
public class RealTimeTopRate {

    public static final Map<String, Object> kafkaParams;

    public static KafkaProducer<String, Object> stringObjectKafkaProducer;

    static {
        Map<String, Object> temp = Maps.newHashMap();
        temp.put("bootstrap.servers", "192.168.1.43:9092");
        temp.put("key.deserializer", StringDeserializer.class);
        temp.put("value.deserializer", StringDeserializer.class);
        temp.put("key.serializer", StringSerializer.class);
        temp.put("value.serializer", StringSerializer.class);
        temp.put("group.id", "wordGroup");
        temp.put("auto.offset.reset", "latest");
        temp.put("enable.auto.commit", false);
        kafkaParams = Collections.unmodifiableMap(temp);

        stringObjectKafkaProducer = new KafkaProducer<>(kafkaParams);
    }

    public static void main(String[] args) throws InterruptedException {
        SparkConf sparkConf = new SparkConf()
                .setMaster("local[*]")
                .setAppName("computeTopRate");
        JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf);
        javaSparkContext.setLogLevel("ERROR");
        javaSparkContext.setCheckpointDir("./checkpoint");
        JavaStreamingContext javaStreamingContext = new JavaStreamingContext(javaSparkContext, Durations.milliseconds(500));

        // kafka连接参数

        // 订阅kafka主题
        List<String> topics = Arrays.asList("recommender");

        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(javaStreamingContext, LocationStrategies.PreferConsistent(), ConsumerStrategies.Subscribe(topics, kafkaParams));
        JavaPairDStream<String, BigDecimal> counts = stream.flatMap(o -> {
                    // 获取kafka消息，并按照|分割  ex: "PRODUCT_RATING_PREFIX:han|奶茶|5.2|1619514025"
                    String[] split = o.value().split("\\|");
                    // 将分割后的数组中的下标为1和下标为2的元素的值按照|连接 ex: "奶茶|5.2"
                    return Lists.newArrayList(split[1] + "|" + split[2]).iterator();
                })
                .mapToPair(o -> {
                    // ex: "奶茶|5.2"
                    String[] split = o.split("\\|");
                    return new Tuple2<>(split[0], new BigDecimal(split[1]));
                })
                .reduceByKey(BigDecimal::add);

        JavaPairDStream<String, BigDecimal> result = counts
                .updateStateByKey(new Function2<List<BigDecimal>, Optional<BigDecimal>, Optional<BigDecimal>>() {

                    private static final long serialVersionUID = 1L;

                    /**
                     * 处理函数
                     *
                     * @param values 经过分组最后 这个key所对应的value
                     * @param state 这个key在本次之前之前的值
                     * @return 处理后的value值
                     */
                    @Override
                    public Optional<BigDecimal> call(List<BigDecimal> values,
                                                  Optional<BigDecimal> state) {

                        BigDecimal updateValue = BigDecimal.ZERO;
                        // 如果原来有值，则先获取原来的值
                        if (state.isPresent()) {
                            updateValue = state.get();
                        }
                        // 加上新分组后的value值
                        for (BigDecimal value : values) {
                            updateValue = updateValue.add(value);
                        }
                        // 给当前key返回新的value值
                        return Optional.of(updateValue);
                    }
                });
        // 发送到kafka的rating主题
        JavaDStream<String> resultDStream = result.map((Function<Tuple2<String, BigDecimal>, String>) stringBigDecimalTuple2 -> String.format("%s,%s", stringBigDecimalTuple2._1(), stringBigDecimalTuple2._2()));
        resultDStream.foreachRDD((VoidFunction<JavaRDD<String>>) stringRDD -> {
            stringObjectKafkaProducer.send(new ProducerRecord<>("rating", "data", JSONUtil.toJsonStr(stringRDD.collect())));
        });
        result.print();

        javaStreamingContext.start();
        javaStreamingContext.awaitTermination();
    }
}
