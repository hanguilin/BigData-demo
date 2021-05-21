package cn.javayuli.common.mock;

import cn.javayuli.common.utils.JDBCUtil;
import com.google.common.collect.Maps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * @author hanguilin
 *
 * 模拟数据
 */
public class DataMock {

    public static void main(String[] args) {
        insertMockContacts();
        insertMockDateDimensions(2020, 2020);
    }

    /**
     * 向数据库插入模拟的时间维度数据
     *
     * @param startYear 开始年份
     * @param endYear 结束年份
     */
    private static void insertMockDateDimensions(Integer startYear, Integer endYear) {
        LocalDate startDate = LocalDate.of(startYear, 1, 1);
        LocalDate endDate = LocalDate.of(endYear, 12, 31);
        Set<String> dateSet = new TreeSet<>();
        Stream.iterate(startDate, o -> o.plusDays(1)).limit(ChronoUnit.DAYS.between(startDate, endDate) + 1).forEach(o -> {
            String year_month_day = DateTimeFormatter.ISO_LOCAL_DATE.format(o);
            String year = year_month_day.substring(0, 4);
            String year_month = year_month_day.substring(0, 7);
            dateSet.add(year);
            dateSet.add(year_month);
            dateSet.add(year_month_day);
        });
        try (
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_dimension_date (year, month, day) values(?, ?, ?)")
        ){
            for (String d: dateSet) {
                String[] split = d.split("-");
                String year = split[0];
                String month = "";
                String day = "";
                if (split.length > 1) {
                    month = split[1];
                }
                if (split.length > 2) {
                    day = split[2];
                }
                preparedStatement.setString(1, year);
                preparedStatement.setString(2, month);
                preparedStatement.setString(3, day);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库插入模拟的联系人数据
     */
    private static void insertMockContacts() {
        HashMap<String, String> contacts = Maps.newHashMap();
        contacts.put("15369468720", "李雁");
        contacts.put("19920860202", "卫艺");
        contacts.put("18411925860", "仰莉");
        contacts.put("14473548449", "陶欣悦");
        contacts.put("18749966182", "施梅梅");
        contacts.put("19379884788", "金虹霖");
        contacts.put("19335715448", "魏明艳");
        contacts.put("18503558939", "华贞");
        contacts.put("13407209608", "华啟倩");
        contacts.put("15596505995", "仲采绿");
        contacts.put("17519874292", "卫丹");
        contacts.put("15178485516", "戚丽红");
        contacts.put("19877232369", "何翠柔");
        contacts.put("18706287692", "钱溶艳");
        contacts.put("18944239644", "钱琳");
        contacts.put("17325302007", "缪静欣");
        contacts.put("18839074540", "焦秋菊");
        contacts.put("19879419704", "吕访琴");
        contacts.put("16480981069", "沈丹");
        contacts.put("18674257265", "褚美丽");
        contacts.put("18302820904", "孙怡");
        contacts.put("15133295266", "许婵");
        contacts.put("17868457605", "曹红恋");
        contacts.put("15490732767", "吕柔");
        contacts.put("15064972307", "冯怜云");

        String insertSQL = "insert into tb_contacts (telephone, name) values (?, ?)";
        try (
                Connection connection = JDBCUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)
        ) {
            for (Map.Entry<String, String> o : contacts.entrySet()) {
                preparedStatement.setString(1, o.getKey());
                preparedStatement.setString(2, o.getValue());
                preparedStatement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
