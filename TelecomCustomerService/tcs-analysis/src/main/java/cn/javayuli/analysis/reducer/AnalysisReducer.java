package cn.javayuli.analysis.reducer;

import cn.javayuli.analysis.kv.AnalysisKey;
import cn.javayuli.analysis.kv.AnalysisValue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * @author hanguilin
 *
 * 分析数据Reducer
 */
public class AnalysisReducer extends Reducer<AnalysisKey, Text, AnalysisKey, AnalysisValue> {

    @Override
    protected void reduce(AnalysisKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        int sumCall = 0;
        int sumDuration = 0;
        for (Text value: values) {
            sumCall ++;
            sumDuration += Integer.valueOf(value.toString());
        }

        context.write(key, new AnalysisValue(sumCall + "", sumDuration + ""));
    }
}
