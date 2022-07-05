package com.main.bd.hadoop.clickstream.pv;

import com.main.bd.hadoop.clickstream.WebLogBean;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class PageViewMapper extends Mapper<LongWritable, Text, Text, WebLogBean> {

    Text k = new Text();
    WebLogBean v = new WebLogBean();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();

        String[] fields = line.split("\001");
        if (fields.length < 9) return;
        //将切分出来的各字段set到weblogbean中
        //fields[0].equals("true")
        v.set("true".equals(fields[0]) ? true : false, fields[1], fields[2], fields[3], fields[4], fields[5], fields[6], fields[7], fields[8]);
        //只有有效记录才进入后续处理
        if (v.isValid()) {
            //此处用ip地址来标识用户
            k.set(v.getRemote_addr());
            context.write(k, v);
        }
    }
}
