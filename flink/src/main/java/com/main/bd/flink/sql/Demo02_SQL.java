package com.main.bd.flink.sql;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.table.api.Table;
import org.apache.flink.table.api.bridge.java.StreamTableEnvironment;

import static org.apache.flink.table.api.Expressions.$;

/**
 * 使用SQL对DataStream中的单词进行统计
 */
public class Demo02_SQL {

    public static void main(String[] args) throws Exception {
        //1.准备环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

        //2.Source
        DataStream<WC> input = env.fromElements(
                new WC("Hello", 1),
                new WC("World", 1),
                new WC("Hello", 1)
        );

        //3.注册表
        tEnv.createTemporaryView("WordCount", input, $("word"), $("frequency"));

        //4.执行查询
        Table resultTable = tEnv.sqlQuery("SELECT word, SUM(frequency) as frequency FROM WordCount GROUP BY word");

        //5.输出结果
        //toAppendStream doesn't support consuming update changes which is produced by node GroupAggregate
//        DataStream<WC> resultDS = tEnv.toAppendStream(resultTable, WC.class);
        DataStream<Tuple2<Boolean, WC>> resultDS = tEnv.toRetractStream(resultTable, WC.class);

        resultDS.print();

        env.execute();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WC {
        public String word;
        public long frequency;
    }

}
