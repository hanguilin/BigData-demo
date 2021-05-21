package cn.javayuli.api.vo;

import java.util.List;

/**
 * @author hanguilin
 *
 * 值对象类
 */
public class DataOutWrapper {

    /**
     * 数据
     */
    List<DataOut> data;

    /**
     * 用户名
     */
    String name;

    public DataOutWrapper() {
    }

    public DataOutWrapper(List<DataOut> data, String name) {
        this.data = data;
        this.name = name;
    }

    public List<DataOut> getData() {
        return data;
    }

    public void setData(List<DataOut> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
