package cn.javayuli.api.vo;

/**
 * @author hanguilin
 *
 * 输出的数据
 */
public class DataOut {

    /**
     * 日期
     */
    private String date;

    /**
     * 通话次数
     */
    private Integer callSum;

    /**
     * 通话总数
     */
    private Integer callDurationSum;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCallSum() {
        return callSum;
    }

    public void setCallSum(Integer callSum) {
        this.callSum = callSum;
    }

    public Integer getCallDurationSum() {
        return callDurationSum;
    }

    public void setCallDurationSum(Integer callDurationSum) {
        this.callDurationSum = callDurationSum;
    }
}
