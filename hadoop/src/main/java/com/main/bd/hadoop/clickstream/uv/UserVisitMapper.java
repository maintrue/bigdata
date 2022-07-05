package com.main.bd.hadoop.clickstream.uv;

import com.main.bd.hadoop.clickstream.PageViewsBean;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class UserVisitMapper extends Mapper<LongWritable, Text, Text, PageViewsBean> {

    PageViewsBean pvBean = new PageViewsBean();
    Text k = new Text();

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

        String line = value.toString();
        String[] fields = line.split("\001");
        int step = Integer.parseInt(fields[5]);
        //(String session, String remote_addr, String timestr, String request, int step, String staylong, String referal, String useragent, String bytes_send, String status)
        //299d6b78-9571-4fa9-bcc2-f2567c46df3472.46.128.140-2013-09-18 07:58:50/hadoop-zookeeper-intro/160"https://www.google.com/""Mozilla/5.0"14722200
        pvBean.set(fields[0], fields[1], fields[2], fields[3],fields[4], step, fields[6], fields[7], fields[8], fields[9]);
        k.set(pvBean.getSession());
        context.write(k, pvBean);

    }

}
