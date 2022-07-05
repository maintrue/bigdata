package com.main.bd.hadoop.mr.topn;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
public class OrderPartition extends Partitioner<OrderBean, Text> {
    @Override
    public int getPartition(OrderBean orderBean, Text text, int i) {
        return (orderBean.getOrderId().hashCode() & 2147483647) % i;
    }
}
