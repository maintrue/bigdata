package com.main.bd.hadoop.mr.topn;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
public class OrderGroupComparator extends WritableComparator {
    public OrderGroupComparator() {
        super(OrderBean.class,true);
    }
    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        //3.1 对形参做强制类型转换
        OrderBean first = (OrderBean)a;
        OrderBean second = (OrderBean)b;
        // 3.2 指定分组规则
        return first.getOrderId().compareTo(second.getOrderId());
    }
}
