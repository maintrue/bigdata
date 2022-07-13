package com.main.bd.flink.endtoend;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class DataProducer {
    public static void main(String[] args) throws InterruptedException {
        Properties props = new Properties();
        props.put("bootstrap.servers", "server1:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> producer = new org.apache.kafka.clients.producer.KafkaProducer<>(props);

        try {
            for (int i = 1; i <= 20; i++) {
                DataBean data = new DataBean(String.valueOf(i));
                ProducerRecord record = new ProducerRecord<String, String>("topic_in", null, null, JSON.toJSONString(data));
                producer.send(record);
                System.out.println("发送数据: " + JSON.toJSONString(data));
                Thread.sleep(2000);
            }
        }catch (Exception e){
            System.out.println(e);
        }
        producer.flush();
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class DataBean {
    private String value;
}