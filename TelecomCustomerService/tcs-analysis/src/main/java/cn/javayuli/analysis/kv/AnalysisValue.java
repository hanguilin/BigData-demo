package cn.javayuli.analysis.kv;

import org.apache.hadoop.io.Writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author hanguilin
 *
 * 自定义分析数据value
 */
public class AnalysisValue implements Writable {

    /**
     * 通话次数求和
     */
    private String callSum;

    /**
     * 通话时间求和
     */
    private String callDurationSum;

    public AnalysisValue() {
    }

    public AnalysisValue(String callSum, String callDurationSum) {
        this.callSum = callSum;
        this.callDurationSum = callDurationSum;
    }

    public String getCallSum() {
        return callSum;
    }

    public void setCallSum(String callSum) {
        this.callSum = callSum;
    }

    public String getCallDurationSum() {
        return callDurationSum;
    }

    public void setCallDurationSum(String callDurationSum) {
        this.callDurationSum = callDurationSum;
    }

    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(callSum);
        dataOutput.writeUTF(callDurationSum);
    }

    @Override
    public void readFields(DataInput dataInput) throws IOException {
        callSum = dataInput.readUTF();
        callDurationSum = dataInput.readUTF();
    }
}
