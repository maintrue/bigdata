package com.main.bd.hadoop.mr.combiner.sort;

import com.main.bd.hadoop.mr.combiner.FlowBean;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
public class CombinerSortMapper extends Mapper<LongWritable, Text,FlowBean, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1:拆分手机号
        String[] split = value.toString().split("\t");
        String phoneNum = split[0];
        //2:获取四个流量字段
        FlowBean flowBean = new FlowBean(Integer.parseInt(split[1]),Integer.parseInt(split[2]),Integer.parseInt(split[3]),Integer.parseInt(split[4]));
        context.write(flowBean,new Text(phoneNum));
    }
}
