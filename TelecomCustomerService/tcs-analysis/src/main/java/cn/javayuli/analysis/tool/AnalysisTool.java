package cn.javayuli.analysis.tool;

import cn.javayuli.analysis.format.MysqlOutputFormat;
import cn.javayuli.analysis.kv.AnalysisKey;
import cn.javayuli.analysis.kv.AnalysisValue;
import cn.javayuli.analysis.mapper.AnalysisMapper;
import cn.javayuli.analysis.reducer.AnalysisReducer;
import cn.javayuli.common.constants.NameConstant;
import cn.javayuli.common.utils.HBaseUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.util.Tool;

/**
 * 分析数据的工具类
 *
 * @author hanguilin
 */
public class AnalysisTool implements Tool {

    private static final String TABLE = HBaseUtil.getHBaseProperties("tableName");

    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance();
        job.setJarByClass(this.getClass());
        Scan scan = new Scan();
        scan.addFamily(Bytes.toBytes(NameConstant.ACTIVE));
        // mapper
        TableMapReduceUtil.initTableMapperJob(TABLE, scan, AnalysisMapper.class, AnalysisKey.class, Text.class, job);
        // reducer
        job.setReducerClass(AnalysisReducer.class);
        job.setOutputKeyClass(AnalysisKey.class);
        job.setOutputValueClass(AnalysisValue.class);
        // outputFormat
        job.setOutputFormatClass(MysqlOutputFormat.class);
        return job.waitForCompletion(true) ? JobStatus.State.SUCCEEDED.getValue() : JobStatus.State.FAILED.getValue();
    }

    @Override
    public void setConf(Configuration configuration) {

    }

    @Override
    public Configuration getConf() {
        return null;
    }
}
