package com.main.bd.flink.transformation;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoMapFunction;

public class ConnectTransformation {

    public static void main(String[] args) throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);

        //2.Source
        DataStream<String> ds1 = env.fromElements("hadoop", "spark", "flink");
        DataStream<Long> ds2 = env.fromElements(1L, 2L, 3L);

        //3.Transformation
        ConnectedStreams<String, Long> tempResult = ds1.connect(ds2);

        //interface CoMapFunction<IN1, IN2, OUT>
        DataStream<String> result = tempResult.map(new CoMapFunction<String, Long, String>() {
            @Override
            public String map1(String value) throws Exception {
                return "String->String:" + value;
            }

            @Override
            public String map2(Long value) throws Exception {
                return "Long->String:" + value.toString();
            }
        });

        result.print();

        //5.execute
        env.execute();
    }

}
