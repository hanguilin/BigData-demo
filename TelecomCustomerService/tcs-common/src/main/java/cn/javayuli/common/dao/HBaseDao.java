package cn.javayuli.common.dao;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Lists;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HBase 工具类
 *
 * @author hanguilin
 */
public class HBaseDao {

    public static Configuration conf;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseDao.class);

    static {
        conf = HBaseConfiguration.create();
    }

    public static void main(String[] args) {
        HBaseDao hBaseDao = new HBaseDao();
//        List<String> tables = hBaseDao.listTable();
//        tables.forEach(System.out::println);
//		// 创建一个列族为name、age、address的名为test_user的表
        hBaseDao.createTable("test_user", Lists.newArrayList("active", "passive"));
        String rowKey = "15870580719_20210512142802_18323797211_1_0600";
        Put put = new Put(rowKey.getBytes());
        put.addColumn("active".getBytes(), "call1".getBytes(), "15870580719".getBytes());
        put.addColumn("active".getBytes(), "call2".getBytes(), "18323797211".getBytes());
        put.addColumn("active".getBytes(), "date_time".getBytes(), "20210512142802".getBytes());
        put.addColumn("active".getBytes(), "duration".getBytes(), "0600".getBytes());
        put.addColumn("active".getBytes(), "flag".getBytes(), "1".getBytes());
        hBaseDao.insertRow("test_user", put);

    }

    /**
     * 表是否存在
     *
     * @param tableName 表名
     * @return
     */
    public boolean isExistTable(String tableName) {
        TableName table = TableName.valueOf(tableName);
        try (
                Connection conn = ConnectionFactory.createConnection(conf);
                Admin admin = conn.getAdmin()
        ) {
            return admin.tableExists(table);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return true;
        }
    }

    /**
     * 创建表
     *
     * @param tableName    表明
     * @param columnFamily 列族
     */
    public void createTable(String tableName, List<String> columnFamily) {
        TableName table = TableName.valueOf(tableName);
        try (
                Connection conn = ConnectionFactory.createConnection(conf);
                Admin admin = conn.getAdmin()
        ) {
            // 判断表是否已存在
            if (admin.tableExists(table)) {
                LOGGER.info("表{}已存在", tableName);
                return;
            }
            TableDescriptorBuilder tableDescriptorBuilder = TableDescriptorBuilder.newBuilder(table);
            // 添加协处理器
            CoprocessorDescriptor coprocessor = CoprocessorDescriptorBuilder
                    .newBuilder("cn.javayuli.coprocessor.observer.CalleeWriteObserver")
                    .setJarPath("hdfs://linuxserver:9000/user/hadoop/hbase/coprocessor/tcs-consumer-coprocessor-1.0.jar")
                    .setPriority(Coprocessor.PRIORITY_USER)
                    .build();
            tableDescriptorBuilder.setCoprocessor(coprocessor);
            if (CollUtil.isNotEmpty(columnFamily)) {
                columnFamily.forEach(column -> {
                    ColumnFamilyDescriptor columnFamilyDescriptor = ColumnFamilyDescriptorBuilder.newBuilder(Bytes.toBytes(column)).build();
                    tableDescriptorBuilder.setColumnFamily(columnFamilyDescriptor);
                });
            }
            admin.createTable(tableDescriptorBuilder.build());
            LOGGER.info("创建表{}成功", tableName);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 删除表
     *
     * @param tableName 表名称
     */
    public void deleteTable(String tableName) {
        TableName table = TableName.valueOf(tableName);
        try (
                Connection conn = ConnectionFactory.createConnection(conf);
                Admin admin = conn.getAdmin()
        ) {
            if (admin.tableExists(table)) {
                // 弃用表
                admin.disableTable(table);
                // 删除表
                admin.deleteTable(table);
                LOGGER.info("删除表{}成功", tableName);
            }
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 获取所有表
     */
    public List<String> listTable() {
        try (
                Connection conn = ConnectionFactory.createConnection(conf);
                Admin admin = conn.getAdmin()
        ) {
            List<TableDescriptor> tableDescriptors = admin.listTableDescriptors();
            return tableDescriptors.stream().map(o -> o.getTableName().getNameAsString()).collect(Collectors.toList());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 插入行数据
     *
     * @param tableName 表名称
     * @param put put对象
     */
    public void insertRow(String tableName, Put put) {
        try (
                Connection conn = ConnectionFactory.createConnection(conf)
        ) {
            Table table = conn.getTable(TableName.valueOf(tableName));
            table.put(put);
            table.close();
            LOGGER.info("向表{}插入数据成功", tableName);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 删除行数据
     *
     * @param tableName 表名称
     * @param rowKey    行键
     * @param colFamily 列族
     * @param col       列名称
     */
    public void deleteRow(String tableName, String rowKey, String colFamily, String col) {
        try (
                Connection conn = ConnectionFactory.createConnection(conf)
        ) {
            Table table = conn.getTable(TableName.valueOf(tableName));
            Delete delete = new Delete(rowKey.getBytes());
            delete.addColumn(colFamily.getBytes(), col.getBytes());
            table.delete(delete);
            System.out.println("删除数据成功");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * 获取行数据
     *
     * @param tableName 表名称
     * @param rowKey    行键
     * @param colFamily 列族
     * @param col       列名称
     */
    public void getRow(String tableName, String rowKey, String colFamily, String col) {
        try (
                Connection conn = ConnectionFactory.createConnection(conf)
        ) {
            Table table = conn.getTable(TableName.valueOf(tableName));
            Get get = new Get(rowKey.getBytes());
            get.addColumn(colFamily.getBytes(), col.getBytes());
            Result result = table.get(get);
            Cell[] rawCells = result.rawCells();
            for (Cell cell : rawCells) {
                System.out.println("RowName:" + new String(CellUtil.cloneRow(cell)) + " ");
                System.out.println("Timetamp:" + cell.getTimestamp() + " ");
                System.out.println("column Family:" + new String(CellUtil.cloneFamily(cell)) + " ");
                System.out.println("row Name:" + new String(CellUtil.cloneQualifier(cell)) + " ");
                System.out.println("value:" + new String(CellUtil.cloneValue(cell)) + " ");
            }
            table.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
