package com.main.bd.hadoop.mr.friends;

import org.apache.hadoop.io.WritableComparable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
public class FriendsBean implements WritableComparable<FriendsBean> {
    private String f1;
    private String f2;
    public FriendsBean(){}
    public FriendsBean(String f1, String f2) {
        this.f1 = f1;
        this.f2 = f2;
    }
    public String getF1() {
        return f1;
    }
    public void setF1(String f1) {
        this.f1 = f1;
    }
    public String getF2() {
        return f2;
    }
    public void setF2(String f2) {
        this.f2 = f2;
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(f1);
        dataOutput.writeUTF(f2);
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.f1 = dataInput.readUTF();
        this.f2 = dataInput.readUTF();
    }
    @Override
    public String toString() {
        return this.f1+this.f2;
    }
    @Override
    public int compareTo(FriendsBean o) {
        int i = this.f1.compareTo(o.getF1());
        if(i == 0){
            return this.f2.compareTo(o.getF2());
        }
        return i;
    }
}
