package com.main.bd.hadoop.mr.combiner.partitioner;

import com.main.bd.hadoop.mr.combiner.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;
public class CombinerPatitioner extends Partitioner<Text, FlowBean> {
    @Override
    public int getPartition(Text text, FlowBean flowBean, int i) {
        String line = text.toString();
        if (line.startsWith("135")){
            return 0;
        }else if(line.startsWith("136")){
            return 1;
        }else if(line.startsWith("137")){
            return 2;
        }else{
            return 3;
        }
    }
}
