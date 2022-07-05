package com.main.bd.hadoop.mr.combiner;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
public class FlowBean implements Writable, WritableComparable<FlowBean> {
    private Integer upPackNum;
    private Integer downPackNum;
    private Integer upPayLoad;
    private Integer downPayLoad;
    public FlowBean(){}
    public FlowBean(Integer upPackNum, Integer downPackNum, Integer upPayLoad, Integer downPayLoad) {
        this.upPackNum = upPackNum;
        this.downPackNum = downPackNum;
        this.upPayLoad = upPayLoad;
        this.downPayLoad = downPayLoad;
    }
    public Integer getUpPackNum() {
        return upPackNum;
    }
    public void setUpPackNum(Integer upPackNum) {
        this.upPackNum = upPackNum;
    }
    public Integer getDownPackNum() {
        return downPackNum;
    }
    public void setDownPackNum(Integer downPackNum) {
        this.downPackNum = downPackNum;
    }
    public Integer getUpPayLoad() {
        return upPayLoad;
    }
    public void setUpPayLoad(Integer upPayLoad) {
        this.upPayLoad = upPayLoad;
    }
    public Integer getDownPayLoad() {
        return downPayLoad;
    }
    public void setDownPayLoad(Integer downPayLoad) {
        this.downPayLoad = downPayLoad;
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(upPackNum);
        dataOutput.writeInt(downPackNum);
        dataOutput.writeInt(upPayLoad);
        dataOutput.writeInt(downPayLoad);
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.upPackNum = dataInput.readInt();
        this.downPackNum = dataInput.readInt();
        this.upPayLoad = dataInput.readInt();
        this.downPayLoad = dataInput.readInt();
    }
    @Override
    public String toString() {
        return this.upPackNum+"\t"
                +this.downPackNum+"\t"
                +this.upPayLoad+"\t"
                +this.downPayLoad;
    }
    @Override
    public int compareTo(FlowBean o) {
        return this.upPayLoad-o.getUpPayLoad();
    }
}
