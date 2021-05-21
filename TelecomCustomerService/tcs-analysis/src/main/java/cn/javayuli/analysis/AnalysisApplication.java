package cn.javayuli.analysis;

import cn.javayuli.analysis.tool.AnalysisTool;
import org.apache.hadoop.util.ToolRunner;

/**
 * @author hanguilin
 *
 * 分析类执行器
 */
public class AnalysisApplication {

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new AnalysisTool(), args);
    }
}
