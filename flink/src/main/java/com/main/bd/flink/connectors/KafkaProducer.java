package com.main.bd.flink.connectors;
import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;

import java.util.Properties;

public class KafkaProducer {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.Source
        DataStreamSource<Student> studentDS = env.fromElements(new Student(1, "tonyma", 18));
        //3.Transformation
        //注意:目前来说我们使用Kafka使用的序列化和反序列化都是直接使用最简单的字符串,所以先将Student转为字符串
        //可以直接调用Student的toString,也可以转为JSON
        SingleOutputStreamOperator<String> jsonDS = studentDS.map(new MapFunction<Student, String>() {
            @Override
            public String map(Student value) throws Exception {
                //String str = value.toString();
                String jsonStr = JSON.toJSONString(value);
                return jsonStr;
            }
        });

        //4.Sink
        jsonDS.print();
        //根据参数创建KafkaProducer/KafkaSink
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "server1:9092");
        FlinkKafkaProducer<String> kafkaSink = new FlinkKafkaProducer<>("flink_kafka",  new SimpleStringSchema(),  props);
        jsonDS.addSink(kafkaSink);

        //5.execute
        env.execute();

        // /export/server/kafka/bin/kafka-console-consumer.sh --bootstrap-server node1:9092 --topic flink_kafka
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Student {
        private Integer id;
        private String name;
        private Integer age;
    }

}
