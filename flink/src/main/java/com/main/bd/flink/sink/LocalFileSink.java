package com.main.bd.flink.sink;

import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
/**
 * Desc
 * 1.ds.print 直接输出到控制台
 * 2.ds.printToErr() 直接输出到控制台,用红色
 * 3.ds.collect 将分布式数据收集为本地集合
 * 4.ds.setParallelism(1).writeAsText("本地/HDFS的path",WriteMode.OVERWRITE)
 */
public class LocalFileSink {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        //2.source
        //DataStream<String> ds = env.fromElements("hadoop", "flink");
        DataStream<String> ds = env.readTextFile("hdfs://192.168.163.140:8020/wordcount_out");

        //3.transformation
        //4.sink
        ds.print();
        ds.printToErr();
        ds.writeAsText("file:///d:/flinkdata", FileSystem.WriteMode.OVERWRITE).setParallelism(2);
        //注意:
        //Parallelism=1为文件
        //Parallelism>1为文件夹

        //5.execute
        env.execute();
    }

}
