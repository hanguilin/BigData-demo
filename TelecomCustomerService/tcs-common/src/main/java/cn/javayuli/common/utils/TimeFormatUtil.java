package cn.javayuli.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * 时间工具类
 *
 * @author hanguilin
 */
public class TimeFormatUtil {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmss");

    private static final Logger LOGGER = LoggerFactory.getLogger(TimeFormatUtil.class);

    /**
     * 格式化日期转换为时间戳
     *
     * @param formatString 格式化日期
     * @return
     * @throws ParseException
     */
    public static String toTS(String formatString) {
        try {
            return String.valueOf(SDF.parse(formatString).getTime());
        } catch (ParseException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }
}
