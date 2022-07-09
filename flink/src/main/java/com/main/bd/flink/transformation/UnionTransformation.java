package com.main.bd.flink.transformation;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class UnionTransformation {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        //2.Source
        DataStream<String> ds1 = env.fromElements("hadoop", "spark", "flink");
        DataStream<String> ds2 = env.fromElements("hadoop", "spark", "flink");

        //3.Transformation
        DataStream<String> result = ds1.union(ds2);//合并但不去重 去重方案链接：https://blog.csdn.net/valada/article/details/104367378

        //4.Sink
        result.print();

        //5.execute
        env.execute();
    }

}
