package com.main.bd.flink.transformation;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.collector.selector.OutputSelector;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SplitStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.ArrayList;

public class SplitSelectTransformation {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        //2.Source
        DataStreamSource<Integer> ds = env.fromElements(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        //3.Transformation
       SplitStream<Integer> splitResult = ds.split(new OutputSelector<Integer>() {
           @Override
           public Iterable<String> select(Integer value) {
               //value是进来的数字
               if (value % 2 == 0) {
                   //偶数
                   ArrayList<String> list = new ArrayList<>();
                   list.add("偶数");
                   return list;
               } else {
                   //奇数
                   ArrayList<String> list = new ArrayList<>();
                   list.add("奇数");
                   return list;
               }
           }
       });
       DataStream<Integer> evenResult = splitResult.select("偶数");
       DataStream<Integer> oddResult = splitResult.select("奇数");

        //4.Sink
        evenResult.print("偶数");
        oddResult.print("奇数");

        //5.execute
        env.execute();
    }

}
