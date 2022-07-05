package com.main.bd.hadoop.mr.sort;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
public class SortMapper extends Mapper<LongWritable, Text,SortBean, NullWritable> {
    private SortBean sortBean = new SortBean();
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] s = value.toString().split(" ");
        sortBean.setWord(s[0]);
        sortBean.setNumber(Integer.valueOf(s[1]));
        context.write(sortBean,NullWritable.get());
    }
}
