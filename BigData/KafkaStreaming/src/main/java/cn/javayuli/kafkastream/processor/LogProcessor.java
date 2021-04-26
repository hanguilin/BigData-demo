package cn.javayuli.kafkastream.processor;


import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

/**
 * 日志预处理
 *
 * @author hanguilin
 */
public class LogProcessor implements Processor<byte[], byte[]> {

    private ProcessorContext context;

    private static final String PRODUCT_RATING_PREFIX = "PRODUCT_RATING_PREFIX:";

    @Override
    public void init(ProcessorContext context) {
        this.context = context;
    }

    @Override
    public void process(byte[] key, byte[] value) {
        String input = new String(value);
        // 根据前缀过滤日志信息，提取后面的内容
        if(input.contains(PRODUCT_RATING_PREFIX)){
            System.out.println("product rating coming!!!!" + input);
            input = input.split(PRODUCT_RATING_PREFIX)[1].trim();
            context.forward("logProcessor".getBytes(), input.getBytes());
        }
    }

    @Override
    public void close() {
    }
}
