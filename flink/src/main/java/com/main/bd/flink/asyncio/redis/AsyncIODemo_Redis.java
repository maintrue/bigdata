package com.main.bd.flink.asyncio.redis;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.AsyncDataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.concurrent.TimeUnit;
/**
 使用异步IO访问redis
hset AsyncReadRedis beijing 1
hset AsyncReadRedis shanghai 2
hset AsyncReadRedis guangzhou 3
hset AsyncReadRedis shenzhen 4
hset AsyncReadRedis hangzhou 5
hset AsyncReadRedis wuhan 6
hset AsyncReadRedis chengdu 7
hset AsyncReadRedis tianjin 8
hset AsyncReadRedis chongqing 9

city.txt
1,beijing
2,shanghai
3,guangzhou
4,shenzhen
5,hangzhou
6,wuhan
7,chengdu
8,tianjin
9,chongqing
 */
public class AsyncIODemo_Redis {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<String> lines = env.readTextFile("file:///d:/city.txt");

        SingleOutputStreamOperator<String> result1 = AsyncDataStream.orderedWait(lines, new AsyncRedis(), 10, TimeUnit.SECONDS, 1);
        SingleOutputStreamOperator<String> result2 = AsyncDataStream.orderedWait(lines, new AsyncRedisByVertx(), 10, TimeUnit.SECONDS, 1);

        result1.print().setParallelism(1);
        result2.print().setParallelism(1);

        env.execute();
    }
}

