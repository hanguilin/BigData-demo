package cn.javayuli.producer;

import cn.javayuli.common.utils.JDBCUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author hanguilin
 *
 * 模拟生成日志信息
 */
public class ProduceLog {

    /**
     * 用户与手机号映射关系
     */
    private static final Map<String, String> USER_PHONE_MAP;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter OUTPUT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private static final Logger LOGGER = LoggerFactory.getLogger(ProduceLog.class);

    private static final String LOG_PREFIX = "TELECOM_CUSTOMER_SERVICE:";

    static {
        HashMap<String, String> contacts = Maps.newHashMap();
        Connection connection = JDBCUtil.getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select telephone, name from tb_contacts")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String telephone = resultSet.getString(1);
                String name = resultSet.getString(2);
                // 从数据库处查询联系人信息
                contacts.put(telephone, name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        USER_PHONE_MAP = Collections.unmodifiableMap(contacts);
    }

    /**
     * 在时间区间内随机选择一个时间
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return
     */
    private static String randomDate(String startDate, String endDate) {
        LocalDateTime start = LocalDateTime.parse(startDate, DATE_TIME_FORMATTER);
        LocalDateTime end = LocalDateTime.parse(endDate, DATE_TIME_FORMATTER);
        long duration = Duration.between(start, end).toMillis();
        LocalDateTime plus = start.plus((long) (Math.random() * duration), ChronoUnit.MILLIS);
        return plus.format(OUTPUT_DATE_TIME_FORMATTER);
    }

    /**
     * 产生单条日志
     *
     * @return
     */
    private static String produceRecord() {
        int size = USER_PHONE_MAP.size();
        // 获取一个[0, size)的随机数
        int fromIdx = new Random().nextInt(size);
        // 通过随机跳过[0, size)个元素随机获取map中的一个key
        String callFrom = USER_PHONE_MAP.entrySet().stream().skip(fromIdx).findFirst().get().getKey();
        // 当被叫人与主叫人相同时则重新随机挑选被叫人
        int toIdx;
        do {
            toIdx = new Random().nextInt(size);
        } while (toIdx == fromIdx);
        String callTo = USER_PHONE_MAP.entrySet().stream().skip(toIdx).findFirst().get().getKey();
        // 随机生成30分钟内的通话时长
        int duration = new Random().nextInt(30 * 60) + 1;
        String durationString = new DecimalFormat("0000").format(duration);
        // 建立通话时间
        String startDate = randomDate("2020-01-01 00:00:00", "2020-12-31 00:00:00");
        // 将信息用逗号拼接
        String log = Lists.newArrayList(callFrom, callTo, startDate, durationString).stream().collect(Collectors.joining(","));
        return log;
    }

    public static void main(String[] args) {
        // 生产日志信息
        while (true) {
            LOGGER.info(LOG_PREFIX + produceRecord());
        }
    }
}
