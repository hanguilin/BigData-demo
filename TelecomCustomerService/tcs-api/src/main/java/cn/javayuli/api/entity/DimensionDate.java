package cn.javayuli.api.entity;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.stream.Collectors;

/**
 * @author hanguilin
 *
 * 时间维度
 */
@TableName("tb_dimension_date")
public class DimensionDate {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String year;

    private String month;

    private String day;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    /**
     * 获取格式化的日期
     *
     * @return
     */
    public String getFormatDate() {
        if (month.length() == 1) {
            month = "0" + month;
        }
        if (day.length() == 1) {
            day = "0" + day;
        }
       return CollUtil.newArrayList(year, month, day).stream().filter(StrUtil::isNotEmpty).collect(Collectors.joining("-"));
    }
}
