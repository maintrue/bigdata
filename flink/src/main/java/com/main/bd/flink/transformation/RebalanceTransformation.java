package com.main.bd.flink.transformation;

//import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class RebalanceTransformation {

    public static void main(String[] args) throws Exception {

//        RebalanceTransformation.normalProccesser();
        RebalanceTransformation.rebalanceProccesser();
    }


    public static void normalProccesser() throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        env.setParallelism(3);

        //2.source
        DataStream<Long> longDS = env.generateSequence(0, 100);

        //3.Transformation
        //下面的操作相当于将数据随机分配一下,有可能出现数据倾斜
        DataStream<Long> filterDS = longDS.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long num) throws Exception {
                return num > 10;
            }
        });

        //接下来使用map操作,将数据转为(分区编号/子任务编号, 数据)
        DataStream<Tuple2<Integer, Integer>> result = filterDS
                .map(new RichMapFunction<Long, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> map(Long value) throws Exception {
                        //获取分区编号/子任务编号
                        int id = getRuntimeContext().getIndexOfThisSubtask();
                        return Tuple2.of(id, 1);
                    }
                }).keyBy(t -> t.f0).sum(1);

        //4.sink
        result.print();//有可能出现数据倾斜

        //5.execute
        env.execute();
    }

    public static void rebalanceProccesser() throws Exception {
        //1.env
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//        env.setRuntimeMode(RuntimeExecutionMode.AUTOMATIC);
        env.setParallelism(3);

        //2.source
        DataStream<Long> longDS = env.generateSequence(0, 100);

        //3.Transformation
        //下面的操作相当于将数据随机分配一下,有可能出现数据倾斜
        DataStream<Long> filterDS = longDS.filter(new FilterFunction<Long>() {
            @Override
            public boolean filter(Long num) throws Exception {
                return num > 10;
            }
        });

        //接下来使用map操作,将数据转为(分区编号/子任务编号, 数据)
        //Rich表示多功能的,比MapFunction要多一些API可以供我们使用
        DataStream<Tuple2<Integer, Integer>> result2 = filterDS
                .rebalance()
                .map(new RichMapFunction<Long, Tuple2<Integer, Integer>>() {
                    @Override
                    public Tuple2<Integer, Integer> map(Long value) throws Exception {
                        //获取分区编号/子任务编号
                        int id = getRuntimeContext().getIndexOfThisSubtask();
                        return Tuple2.of(id, 1);
                    }
                }).keyBy(t -> t.f0).sum(1);

        //4.sink
        result2.print();//在输出前进行了rebalance重分区平衡,解决了数据倾斜

        //5.execute
        env.execute();
    }
}
