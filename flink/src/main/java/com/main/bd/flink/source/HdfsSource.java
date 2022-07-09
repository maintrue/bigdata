package com.main.bd.flink.source;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.typeutils.base.StringSerializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;

public class HdfsSource {
    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();


//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        //2.source
        // * 1.env.readTextFile(本地文件/HDFS文件);//压缩文件也可以
//        DataStream<String> ds1 = env.readTextFile("data/input/words.txt");
        DataStream<String> ds2 = env.readTextFile("hdfs://192.168.163.140:8020/wordcount_out");
//        DataStream<String> ds3 = env.readTextFile("hdfs://192.168.163.140:8020/a/b/c/NOTICE.txt");
//        DataStream<String> ds4 = env.readTextFile("data/input/wordcount.txt.gz");
        //3.Transformation
        //4.sink
//        ds1.print();
        ds2.print();
//        ds3.print();
//        ds4.print();
        //5.execute
        env.execute();
    }
}
