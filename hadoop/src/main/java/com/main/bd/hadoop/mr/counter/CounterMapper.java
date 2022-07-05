package com.main.bd.hadoop.mr.counter;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
public class CounterMapper extends Mapper<LongWritable, Text,Text, NullWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        Counter counter = context.getCounter("MY_MAPPER_COUNT", "MyMapperCounter");
        counter.increment(1l);
        context.write(value,NullWritable.get());
    }
}
