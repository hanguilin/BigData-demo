package cn.javayuli.processor;

import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.api.java.UDF1;
import org.apache.spark.sql.types.DataTypes;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 统计类
 *
 * @author hanguilin
 */
public class Statistics {

    /**
     * 历史热门商品集合
     */
    private static final String COLLECTION_HOT = "RateHotProducts";

    /**
     * 历史热门商品集合
     */
    private static final String COLLECTION_HOT_RECENTLY = "RateHotRecentlyProducts";

    /**
     * 商品平均得分集合
     */
    private static final String COLLECTION_AVERAGE = "AverageProducts";

    /**
     * 时间序列化
     */
    private static DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyyMM");

    /**
     * 创建SparkSession
     *
     * @return
     */
    private static SparkSession createSparkSession() {
        String uri = "mongodb://192.168.1.43:27017/bigData.Ratings";
        SparkSession sparkSession = SparkSession.builder()
                .appName("DataLoader")
                .master("local[*]")
                .config("spark.app.id", "DataLoader")
                .config("spark.mongodb.input.uri", uri)
                .config("spark.mongodb.output.uri", uri)
                .getOrCreate();
        return sparkSession;
    }

    public static void main(String[] args) {
        SparkSession sparkSession = createSparkSession();
        JavaSparkContext sparkContext = new JavaSparkContext(sparkSession.sparkContext());
        Dataset<Row> rowDataset = MongoSpark.load(sparkContext).toDF();
        // 打印schema
        rowDataset.printSchema();
        // 打印数据
        rowDataset.show();
        // 内存中创建一个临时表Ratings
        rowDataset.createOrReplaceTempView("Ratings");

        // *****************历史热门商品统计******************
        Dataset<Row> rateHotProducts = sparkSession.sql("select productId, count(productId) as count from Ratings group by productId");
        MongoSpark.write(rateHotProducts).option("collection", COLLECTION_HOT).mode("overwrite").format("com.mongodb.spark.sql").save();

        // *****************最近热门商品统计******************
        // 注册一个UDF函数，用于将timestamp转换成年月格式
        sparkSession.udf().register("changeDate", (UDF1<Integer, Integer>) (parameter) -> Integer.valueOf(Instant.ofEpochSecond(parameter).atZone(ZoneId.systemDefault()).toLocalDate().format(YEAR_MONTH)), DataTypes.IntegerType);
        // 将原来的Rating数据集中的时间转换成年月的格式
        Dataset<Row> ratingOfYearMonth = sparkSession.sql("select productId, score, changeDate(timestamp) as yearmonth from Ratings");
        // 将新的数据集注册成为一张表
        ratingOfYearMonth.createOrReplaceTempView("RatingOfMonth");
        // 根据月份和商品进行统计，并按年月与数量进行倒序排序
        Dataset<Row> rateHotRecentlyProducts = sparkSession.sql("select productId, count(productId) as count ,yearmonth from RatingOfMonth group by yearmonth,productId order by yearmonth desc, count desc");
        MongoSpark.write(rateHotRecentlyProducts).option("collection", COLLECTION_HOT_RECENTLY).mode("overwrite").format("com.mongodb.spark.sql").save();

        // *****************商品平均得分统计******************
        Dataset<Row> averageProducts = sparkSession.sql("select productId, avg(score) as avg from Ratings group by productId");
        MongoSpark.write(averageProducts).option("collection", COLLECTION_AVERAGE).mode("overwrite").format("com.mongodb.spark.sql").save();
    }
}
