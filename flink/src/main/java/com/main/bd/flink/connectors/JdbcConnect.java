package com.main.bd.flink.connectors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class JdbcConnect {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2.Source
        env.fromElements(new Student(null, "cnm", 18))
                //3.Transformation
                //4.Sink
                .addSink(JdbcSink.sink(
                        "INSERT INTO `t_student` (`id`, `name`, `age`) VALUES (null, ?, ?)",
                        (ps, s) -> {
                            ps.setString(1, s.getName());
                            ps.setInt(2, s.getAge());
                        },
                        new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                                .withUrl("jdbc:mysql://localhost:3306/test")
                                .withUsername("root")
                                .withPassword("root")
                                .withDriverName("com.mysql.jdbc.Driver")
                                .build()));
        //5.execute
        env.execute();
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
