package com.main.bd.flink.connectors;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.util.Collector;

import java.util.Properties;

/**
 * Author itcast
 * Desc
 * 需求:使用flink-connector-kafka_2.12中的FlinkKafkaConsumer消费Kafka中的数据做WordCount
 * 需要设置如下参数:
 * 1.订阅的主题
 * 2.反序列化规则
 * 3.消费者属性-集群地址
 * 4.消费者属性-消费者组id(如果不设置,会有默认的,但是默认的不方便管理)
 * 5.消费者属性-offset重置规则,如earliest/latest...
 * 6.动态分区检测(当kafka的分区数变化/增加时,Flink能够检测到!)
 * 7.如果没有设置Checkpoint,那么可以设置自动提交offset,后续学习了Checkpoint会把offset随着做Checkpoint的时候提交到Checkpoint和默认主题中
 */
public class KafkaComsumer {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.Source
        Properties props  = new Properties();
        props.setProperty("bootstrap.servers", "server1:9092");
        props.setProperty("group.id", "flink");
        props.setProperty("auto.offset.reset","latest");
        props.setProperty("flink.partition-discovery.interval-millis","5000");//会开启一个后台线程每隔5s检测一下Kafka的分区情况
        props.setProperty("enable.auto.commit", "true");
        props.setProperty("auto.commit.interval.ms", "2000");
        //kafkaSource就是KafkaConsumer
        FlinkKafkaConsumer<String> kafkaSource = new FlinkKafkaConsumer<>("flink_kafka", new SimpleStringSchema(), props);
        kafkaSource.setStartFromGroupOffsets();//设置从记录的offset开始消费,如果没有记录从auto.offset.reset配置开始消费
        //kafkaSource.setStartFromEarliest();//设置直接从Earliest消费,和auto.offset.reset配置无关
        DataStreamSource<String> kafkaDS = env.addSource(kafkaSource);

        //3.Transformation
        //3.1切割并记为1
        SingleOutputStreamOperator<Tuple2<String, Integer>> wordAndOneDS = kafkaDS.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
                String[] words = value.split(" ");
                for (String word : words) {
                    out.collect(Tuple2.of(word, 1));
                }
            }
        });
        //3.2分组
        KeyedStream<Tuple2<String, Integer>, Tuple> groupedDS = wordAndOneDS.keyBy(0);
        //3.3聚合
        SingleOutputStreamOperator<Tuple2<String, Integer>> result = groupedDS.sum(1);

        //4.Sink
        result.print();

        //5.execute
        env.execute();
    }

}
