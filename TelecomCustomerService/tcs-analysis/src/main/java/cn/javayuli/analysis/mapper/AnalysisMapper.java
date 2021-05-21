package cn.javayuli.analysis.mapper;

import cn.javayuli.analysis.kv.AnalysisKey;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;

import java.io.IOException;

/**
 * Mapper
 *
 * @author hanguilin
 */
public class AnalysisMapper extends TableMapper<AnalysisKey, Text> {

    @Override
    protected void map(ImmutableBytesWritable key, Result value, Context context) throws IOException, InterruptedException {
        // 19920860202_20201226131016_18503558939_0_1451
        String rowKey = Bytes.toString(key.get());
        String[] split = rowKey.split("_");

        String call1 = split[0];
        String dateTime = split[1];
        String call2 = split[2];
        String duration = split[4];

        String year = dateTime.substring(0, 4);
        String month = dateTime.substring(0, 6);
        String date = dateTime.substring(0, 8);

        // 主叫用户 - 年
        context.write(new AnalysisKey(call1, year), new Text(duration));
        // 主叫用户 - 月
        context.write(new AnalysisKey(call1, month), new Text(duration));
        // 主叫用户 - 日
        context.write(new AnalysisKey(call1, date), new Text(duration));

        // 被叫用户 - 年
        context.write(new AnalysisKey(call2, year), new Text(duration));
        // 被叫用户 - 月
        context.write(new AnalysisKey(call2, month), new Text(duration));
        // 被叫用户 - 日
        context.write(new AnalysisKey(call2, date), new Text(duration));
    }
}
