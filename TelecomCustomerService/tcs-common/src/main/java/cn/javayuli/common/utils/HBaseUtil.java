package cn.javayuli.common.utils;

import cn.hutool.setting.dialect.Props;

/**
 * HBase工具类
 * @author hanguilin
 */
public class HBaseUtil {

    private static final Props HBASE_PROPS = new Props("classpath://hbase.properties");

    /**
     * 生成行键
     *
     * @param call1
     * @param call2
     * @param dateTime
     * @param flag
     * @param duration
     * @return
     */
    public static String genRowKey (String call1, String dateTime, String call2, String flag, String duration) {
        return call1 + "_" + dateTime + "_" + call2 + "_" + flag + "_" + duration;
    }

    /**
     * 获取配置的hbase属性
     *
     * @param key 键
     * @return
     */
    public static String getHBaseProperties(String key) {
       return HBASE_PROPS.getStr(key);
    }
}
