package com.main.bd.hadoop.mr.combiner.partitioner;

import com.main.bd.hadoop.mr.combiner.FlowBean;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class CombinerPartitionerMapper extends Mapper<LongWritable, Text,Text, FlowBean> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1:拆分手机号
        String[] split = value.toString().split("\t");
        String phoneNum = split[1];
        //2:获取四个流量字段
        FlowBean flowBean = new FlowBean(Integer.parseInt(split[6]),Integer.parseInt(split[7]),Integer.parseInt(split[8]),Integer.parseInt(split[9]));
        context.write(new Text(phoneNum),flowBean);
    }
}
