package com.main.bd.hadoop.mr.topn;

import org.apache.hadoop.io.WritableComparable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
public class OrderBean implements WritableComparable<OrderBean> {
    private String orderId;
    private Double money;
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public Double getMoney() {
        return money;
    }
    public void setMoney(Double money) {
        this.money = money;
    }
    @Override
    public int compareTo(OrderBean o) {
        int i = this.orderId.compareTo(o.getOrderId());
        if(i==0){
            return this.money.compareTo(o.getMoney()) * -1;
        }
        return i;
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.orderId);
        dataOutput.writeDouble(this.money);
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.orderId = dataInput.readUTF();
        this.money = dataInput.readDouble();
    }
    @Override
    public String toString() {
        return "OrderBean{" +
                "orderId='" + orderId + '\'' +
                ", money=" + money +
                '}';
    }
}
