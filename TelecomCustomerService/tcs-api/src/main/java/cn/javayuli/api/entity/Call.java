package cn.javayuli.api.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * @author hanguilin
 *
 * 通话信息
 */
@TableName("tb_call")
public class Call {

    /**
     * 复合主键（联系人维度id，时间维度id）
     */
    @TableId
    private String idDateContact;

    /**
     * 时间维度id
     */
    private Integer idDateDimension;

    /**
     * 查询人的id
     */
    private Integer idContact;

    /**
     * 通话次数总和
     */
    private Integer callSum;

    /**
     * 通话时长总和
     */
    private Integer callDurationSum;

    public String getIdDateContact() {
        return idDateContact;
    }

    public void setIdDateContact(String idDateContact) {
        this.idDateContact = idDateContact;
    }

    public Integer getIdDateDimension() {
        return idDateDimension;
    }

    public void setIdDateDimension(Integer idDateDimension) {
        this.idDateDimension = idDateDimension;
    }

    public Integer getIdContact() {
        return idContact;
    }

    public void setIdContact(Integer idContact) {
        this.idContact = idContact;
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
