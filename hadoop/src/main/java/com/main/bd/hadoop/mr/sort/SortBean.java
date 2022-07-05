package com.main.bd.hadoop.mr.sort;


import org.apache.hadoop.io.WritableComparable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
public class SortBean implements WritableComparable<SortBean> {
    private String word;
    private int number;
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        this.number = number;
    }
    @Override
    public int compareTo(SortBean o) {
        String word = o.getWord();
        int result = this.word.compareTo(word);
        if(result == 0){
            return o.getNumber()-this.getNumber();
        }else{
            return result;
        }
    }
    @Override
    public void write(DataOutput dataOutput) throws IOException {
        dataOutput.writeUTF(this.word);
        dataOutput.writeInt(this.number);
    }
    @Override
    public void readFields(DataInput dataInput) throws IOException {
        this.word = dataInput.readUTF();
        this.number = dataInput.readInt();
    }
    @Override
    public String toString() {
        return this.word+" "+this.number;
    }
}
