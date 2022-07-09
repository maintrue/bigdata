package com.main.bd.flink.source;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

import java.util.Random;
import java.util.UUID;

public class MyRichParallelSource {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        //2.Source
        DataStream<Order> orderDS = env
                .addSource(new MyOrderSource())
                .setParallelism(2);

        //3.Transformation

        //4.Sink
        orderDS.print();
        //5.execute
        env.execute();
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order {
        private String id;
        private Integer userId;
        private Integer money;
        private Long createTime;
    }

    public static class MyOrderSource extends RichParallelSourceFunction<Order> {
        private Boolean flag = true;
        @Override
        public void run(SourceContext<Order> ctx) throws Exception {
            Random random = new Random();
            while (flag){
                Thread.sleep(1000);
                String id = UUID.randomUUID().toString();
                int userId = random.nextInt(3);
                int money = random.nextInt(101);
                long createTime = System.currentTimeMillis();
                ctx.collect(new Order(id,userId,money,createTime));
            }
        }
        //取消任务/执行cancle命令的时候执行
        @Override
        public void cancel() {
            flag = false;
        }
    }

}


