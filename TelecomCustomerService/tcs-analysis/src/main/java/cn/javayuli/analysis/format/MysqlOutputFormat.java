package cn.javayuli.analysis.format;

import cn.javayuli.analysis.kv.AnalysisKey;
import cn.javayuli.analysis.kv.AnalysisValue;
import cn.javayuli.common.utils.JDBCUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 输出到mysql
 *
 * @author hanguilin
 */
public class MysqlOutputFormat extends OutputFormat<AnalysisKey, AnalysisValue> {

    private FileOutputCommitter committer = null;

    protected static class MysqlRecordWriter extends RecordWriter<AnalysisKey, AnalysisValue> {

        /**
         * Mysql连接
         */
        private Connection connection;
        /**
         * 用户信息Map
         */
        private Map<String, Integer> userMap = new HashMap();
        /**
         * 时间维度Map
         */
        private Map<String, Integer> dateMap = new HashMap();

        public MysqlRecordWriter() {
            connection = JDBCUtil.getConnection();
            try (PreparedStatement contactStatement = connection.prepareStatement("select id, telephone from tb_contacts");
                 PreparedStatement dateStatement = connection.prepareStatement("select id, year, month, day from tb_dimension_date")){
                ResultSet contactRs = contactStatement.executeQuery();
                while (contactRs.next()) {
                    Integer id = contactRs.getInt(1);
                    String telephone = contactRs.getString(2);
                    userMap.put(telephone, id);
                }
                ResultSet dateRs = dateStatement.executeQuery();
                while (dateRs.next()) {
                    Integer id = dateRs.getInt(1);
                    String year = dateRs.getString(2);
                    String month = dateRs.getString(3);
                    String day = dateRs.getString(4);
                    if (month.length() == 1) {
                        month = "0" + month;
                    }
                    if (day.length() == 1) {
                        day = "0" + day;
                    }
                    dateMap.put(year + month + day, id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void write(AnalysisKey analysisKey, AnalysisValue analysisValue) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("insert into tb_call (id_date_contact, id_date_dimension, id_contact, call_sum, call_duration_sum) values (?, ?, ?, ?, ?)")) {
                Integer idDateDimension = dateMap.get(analysisKey.getDate());
                Integer idContact = userMap.get(analysisKey.getTelephone());
                Integer callSum = Integer.valueOf(analysisValue.getCallSum());
                Integer callDurationSum = Integer.valueOf(analysisValue.getCallDurationSum());
                preparedStatement.setString(1, idDateDimension + "_" + idContact);
                preparedStatement.setInt(2, idDateDimension);
                preparedStatement.setInt(3, idContact);
                preparedStatement.setInt(4, callSum);
                preparedStatement.setInt(5, callDurationSum);
                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void close(TaskAttemptContext taskAttemptContext) {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public RecordWriter getRecordWriter(TaskAttemptContext taskAttemptContext) {
        return new MysqlRecordWriter();
    }

    @Override
    public void checkOutputSpecs(JobContext jobContext) {

    }

    public static Path getOutputPath(JobContext job) {
        String name = job.getConfiguration().get(FileOutputFormat.OUTDIR);
        return name == null ? null : new Path(name);
    }

    @Override
    public OutputCommitter getOutputCommitter(TaskAttemptContext taskAttemptContext) {
        return Optional.ofNullable(committer).orElseGet(() -> {
            Path outputPath = getOutputPath(taskAttemptContext);
            FileOutputCommitter fileOutputCommitter = null;
            try {
                fileOutputCommitter = new FileOutputCommitter(outputPath, taskAttemptContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return fileOutputCommitter;
        });
    }
}
