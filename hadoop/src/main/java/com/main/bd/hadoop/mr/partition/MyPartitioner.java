package com.main.bd.hadoop.mr.partition;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
public class MyPartitioner extends Partitioner<Text, NullWritable> {
    @Override
    public int getPartition(Text text, NullWritable nullWritable, int i) {
        if(text.toString().contains("job")){
            return 0;
        }else{
            return 1;
        }
    }
}
