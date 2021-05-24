package cn.javayuli.consumer;

import cn.hutool.setting.dialect.Props;
import cn.javayuli.common.constants.NameConstant;
import cn.javayuli.common.constants.StateConstant;
import cn.javayuli.common.dao.HBaseDao;
import cn.javayuli.common.utils.HBaseUtil;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;

/**
 * kafka消费者
 *
 * @author hanguilin
 */
public class HBaseConsumer {

    private static final String LOG_PREFIX = "TELECOM_CUSTOMER_SERVICE:";

    public static void main(String[] args) {
        Props kafkaProps = new Props("classpath://kafka.properties");
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(kafkaProps);
        kafkaConsumer.subscribe(Collections.singletonList(kafkaProps.getStr("kafka.topic")));
        HBaseDao hBaseDao = new HBaseDao();
        // 创建表
        String tableName = HBaseUtil.getHBaseProperties("tableName");
        hBaseDao.createTable(tableName, Lists.newArrayList(NameConstant.ACTIVE, NameConstant.PASSIVE));
        byte[] ACTIVE = Bytes.toBytes(NameConstant.ACTIVE);
        byte[] FLAG = Bytes.toBytes(StateConstant.ACTIVE);
        while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records) {
                    // 15490732767,19335715448,20200505161114,0271
                    String value = record.value().split(LOG_PREFIX)[1].trim();
                    String[] split = value.split(",");
                    String rowKey = HBaseUtil.genRowKey(split[0], split[2], split[1], StateConstant.ACTIVE, split[3]);
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(ACTIVE, Bytes.toBytes("call1"), Bytes.toBytes(split[0]));
                    put.addColumn(ACTIVE, Bytes.toBytes("call2"), Bytes.toBytes(split[1]));
                    put.addColumn(ACTIVE, Bytes.toBytes("date_time"), Bytes.toBytes(split[2]));
                    put.addColumn(ACTIVE, Bytes.toBytes("duration"), Bytes.toBytes(split[3]));
                    put.addColumn(ACTIVE, Bytes.toBytes("flag"), FLAG);
                    hBaseDao.insertRow(tableName, put);
            }
        }
    }
}
