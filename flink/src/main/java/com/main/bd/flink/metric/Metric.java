package com.main.bd.flink.metric;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.Counter;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class Metric {

    public static void main(String[] args) throws Exception {
        //1.准备环境-env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        //2.准备数据-source
        //2.source
        DataStream<String> linesDS = env.socketTextStream("server1", 9999);
        //3.处理数据-transformation
        DataStream<String> wordsDS = linesDS.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                //value就是一行行的数据
                String[] words = value.split(" ");
                for (String word : words) {
                    out.collect(word);//将切割处理的一个个的单词收集起来并返回
                }
            }
        });
        //3.2对集合中的每个单词记为1
        DataStream<Tuple2<String, Integer>> wordAndOnesDS = wordsDS.map(new RichMapFunction<String, Tuple2<String, Integer>>() {
            Counter myCounter;
            @Override
            public void open(Configuration parameters) throws Exception {
                myCounter= getRuntimeContext().getMetricGroup().addGroup("myGroup").counter("myCounter");
            }

            @Override
            public Tuple2<String, Integer> map(String value) throws Exception {
                myCounter.inc();
                //value就是进来一个个的单词
                return Tuple2.of(value, 1);
            }
        });
        //3.3对数据按照单词(key)进行分组
        KeyedStream<Tuple2<String, Integer>, String> groupedDS = wordAndOnesDS.keyBy(t -> t.f0);
        //3.4对各个组内的数据按照数量(value)进行聚合就是求sum
        DataStream<Tuple2<String, Integer>> result = groupedDS.sum(1);

        //4.输出结果-sink
        result.print().name("mySink");

        //5.触发执行-execute
        env.execute();
    }

}
