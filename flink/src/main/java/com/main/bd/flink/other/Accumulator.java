package com.main.bd.flink.other;

import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.operators.MapOperator;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;

public class Accumulator {

    public static void main(String[] args) throws Exception {
        //1.env
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();

        //2.Source
        DataSource<String> dataDS = env.fromElements("aaa", "bbb", "ccc", "ddd");

        //3.Transformation
        MapOperator<String, String> result = dataDS.map(new RichMapFunction<String, String>() {
            //-1.创建累加器
            private IntCounter elementCounter = new IntCounter();
            Integer count = 0;

            @Override
            public void open(Configuration parameters) throws Exception {
                super.open(parameters);
                //-2注册累加器
                getRuntimeContext().addAccumulator("elementCounter", elementCounter);
            }

            @Override
            public String map(String value) throws Exception {
                //-3.使用累加器
                this.elementCounter.add(1);
                count+=1;
                System.out.println("不使用累加器统计的结果:"+count);
                return value;
            }
        }).setParallelism(2);

        //4.Sink
        result.writeAsText("file:///d:/data/output/test", FileSystem.WriteMode.OVERWRITE);

        //5.execute
        //-4.获取加强结果
        JobExecutionResult jobResult = env.execute();
        int nums = jobResult.getAccumulatorResult("elementCounter");
        System.out.println("使用累加器统计的结果:"+nums);
    }

}
