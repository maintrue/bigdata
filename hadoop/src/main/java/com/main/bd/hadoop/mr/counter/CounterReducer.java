package com.main.bd.hadoop.mr.counter;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CounterReducer extends Reducer<Text, NullWritable,Text,NullWritable> {
    public static enum Counter{
       MY_REDUCE_COUNTER
   }
    @Override
    protected void reduce(Text key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {
        context.getCounter(Counter.MY_REDUCE_COUNTER).increment(1l);
        context.write(key,NullWritable.get());
    }
}
