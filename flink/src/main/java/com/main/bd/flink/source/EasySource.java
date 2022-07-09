package com.main.bd.flink.source;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Arrays;

public class EasySource {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        //2.source
        // * 1.env.fromElements(可变参数);
        DataStream<String> ds1 = env.fromElements("hadoop", "spark", "flink");
        // * 2.env.fromColletion(各种集合);
        DataStream<String> ds2 = env.fromCollection(Arrays.asList("hadoop", "spark", "flink"));
        // * 3.env.generateSequence(开始,结束);
        DataStream<Long> ds3 = env.generateSequence(1, 10);
        //* 4.env.fromSequence(开始,结束);
//        DataStream<Long> ds4 = env.fromSequence(1, 10);
        //3.Transformation
        //4.sink
        ds1.print();
        ds2.print();
        ds3.print();
//        ds4.print();
        //5.execute
        env.execute();
    }

}
