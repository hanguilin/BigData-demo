package cn.javayuli.processor;


import cn.hutool.json.JSONUtil;
import cn.javayuli.entity.Product;
import cn.javayuli.entity.Rating;
import com.google.common.collect.Maps;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.config.WriteConfig;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.bson.Document;

import java.util.HashMap;


/**
 * 加载数据类
 *
 * @author hanguilin
 */
public class Loader {

    /**
     * 创建SparkContext
     *
     * @return
     */
    private static JavaSparkContext createJavaSparkContext() {
        String uri = "mongodb://192.168.1.43:27017/bigData.coll";
        SparkConf sparkConf = new SparkConf()
                .setAppName("DataLoader")
                .setMaster("local[*]")
                .set("spark.app.id", "DataLoader")
                .set("spark.mongodb.input.uri", uri)
                .set("spark.mongodb.output.uri", uri);
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        return sparkContext;
    }

    /**
     * 加载数据写入mongodb
     *
     * @param context spark context
     * @param filePath 文件路径
     * @param collection mongodb中集合名称
     * @param mapFunc String RDD转换为Entity RDD的转换函数
     * @param <T> 泛型T
     */
    private static <T> void writeDataToMongo(JavaSparkContext context, String filePath, String collection, Function<String,T> mapFunc) {
        // 读取文件成RDD
        JavaRDD<String> stringRDD = context.textFile(filePath);
        // String类型RDD调用map函数转换成实体类的RDD
        JavaRDD<T> entityRDD = stringRDD.map(mapFunc);
        // 实体类的RDD调用map函数转换成MongoDB中Document的RDD
        JavaRDD<Document> documentRDD = entityRDD.map((Function<T, Document>) entityDocument -> Document.parse(JSONUtil.toJsonStr(entityDocument)));
        // 配置MongoDB写入参数
        HashMap<String, String> writeOverrides = Maps.newHashMap();
        writeOverrides.put("collection", collection);
        writeOverrides.put("writeConcern.w", "majority");
        WriteConfig writeConfig = WriteConfig.create(context).withOptions(writeOverrides);
        // 插入数据库
        MongoSpark.save(documentRDD, writeConfig);
    }

    public static void main(String[] args) {
        JavaSparkContext sparkContext = createJavaSparkContext();
        // Product转换函数
        Function<String, Product> productMapFunc = (item) -> {
            String[] split = item.split("\\^");
            return new Product(Integer.valueOf(split[0]), split[1].trim(), split[4].trim(), split[5].trim(), split[6].trim());
        };
        writeDataToMongo(sparkContext, "hdfs://localhost:9000/user/hadoop/input/products.csv", Product.COLLECTION_PRODUCT, productMapFunc);
        // Rating转换函数
        Function<String, Rating> ratingMapFunc = (item) -> {
            String[] split = item.split(",");
            return new Rating(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Double.valueOf(split[2]), Integer.valueOf(split[3]));
        };
        writeDataToMongo(sparkContext, "hdfs://localhost:9000/user/hadoop/input/ratings.csv", Rating.COLLECTION_RATING, ratingMapFunc);
    }
}
