package cn.javayuli.analysis.kv;

import org.apache.hadoop.io.WritableComparable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author hanguilin
 *
 * 自定义分析数据Key
 */
public class AnalysisKey implements WritableComparable<AnalysisKey> {

    /**
     * 电话
     */
    private String telephone;
    /**
     * 日期
     */
    private String date;

    public AnalysisKey() {
    }

    public AnalysisKey(String telephone, String date) {
        this.telephone = telephone;
        this.date = date;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int compareTo(AnalysisKey o) {
        int result = telephone.compareTo(o.getTelephone());
        if (result == 0) {
            result = date.compareTo(o.getDate());
        }
        return result;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(telephone);
        dataOutput.writeUTF(date);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        telephone = dataInput.readUTF();
        date = dataInput.readUTF();
    }
}
