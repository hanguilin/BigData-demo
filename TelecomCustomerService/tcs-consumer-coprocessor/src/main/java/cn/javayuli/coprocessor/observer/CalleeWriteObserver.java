package cn.javayuli.coprocessor.observer;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import cn.javayuli.common.constants.NameConstant;
import cn.javayuli.common.constants.StateConstant;
import cn.javayuli.common.dao.HBaseDao;
import cn.javayuli.common.utils.HBaseUtil;
import cn.javayuli.common.utils.TimeFormatUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.coprocessor.ObserverContext;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessor;
import org.apache.hadoop.hbase.coprocessor.RegionCoprocessorEnvironment;
import org.apache.hadoop.hbase.coprocessor.RegionObserver;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.wal.WALEdit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * HBase协处理器，用于实现主叫日志插入成功后，同时插入一条被叫日志
 *
 * @author hanguilin
 */
public class CalleeWriteObserver implements RegionObserver, RegionCoprocessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalleeWriteObserver.class);

    private static final byte[] PASSIVE = Bytes.toBytes(NameConstant.PASSIVE);

    private static final byte[] FLAG = Bytes.toBytes(StateConstant.PASSIVE);

    private static final String TABLE = HBaseUtil.getHBaseProperties("tableName");

    /**
     * hbase 2.x需要重写的方法
     *
     * @return
     */
    @Override
    public Optional<RegionObserver> getRegionObserver() {
        return Optional.of(this);
    }

    /**
     * put数据之后执行的操作
     *
     * @param c
     * @param put
     * @param edit
     * @param durability
     * @throws IOException
     */
    @Override
    public void postPut(ObserverContext<RegionCoprocessorEnvironment> c, Put put, WALEdit edit, Durability durability) {
       try {
           String currentTableName = c.getEnvironment().getRegionInfo().getTable().getNameAsString();

           if (!StrUtil.equals(currentTableName, TABLE)) {
               return;
           }
           // 15870580719_20210512142802_18323797211_1_0600
           String originRowKey = Bytes.toString(put.getRow());
           String[] splits = originRowKey.split("_");
           String flag = splits[3];
           if (!StrUtil.equals(StateConstant.ACTIVE, flag)) {
               return;
           }
           String rowKey = HBaseUtil.genRowKey(splits[2], splits[1], splits[0], StateConstant.PASSIVE, splits[4]);
           Put newPut = new Put(rowKey.getBytes());

           newPut.addColumn(PASSIVE, Bytes.toBytes("call1"), Bytes.toBytes(splits[2]));
           newPut.addColumn(PASSIVE, Bytes.toBytes("call2"), Bytes.toBytes(splits[0]));
           newPut.addColumn(PASSIVE, Bytes.toBytes("date_time"), Bytes.toBytes(splits[1]));
           newPut.addColumn(PASSIVE, Bytes.toBytes("duration"), Bytes.toBytes(splits[4]));
           newPut.addColumn(PASSIVE, Bytes.toBytes("flag"), FLAG);
           newPut.addColumn(PASSIVE, Bytes.toBytes("date_time_ts"), Bytes.toBytes(TimeFormatUtil.toTS(splits[1])));
           Table table = c.getEnvironment().getConnection().getTable(TableName.valueOf(TABLE));
           table.put(newPut);
           table.close();
       } catch (IOException e) {
           LOGGER.error(e.getMessage(), e);
       }
    }
}
