package com.main.bd.hadoop.mr.combiner.partitioner;

import com.main.bd.hadoop.mr.combiner.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class CombinerPartitionerReducer extends Reducer<Text, FlowBean, Text,FlowBean> {
    private FlowBean result;
    @Override
    protected void reduce(Text key, Iterable<FlowBean> values, Context context) throws IOException, InterruptedException {
        Integer upPackNum = 0;
        Integer downPackNum = 0;
        Integer upPayLoad = 0;
        Integer downPayLoad = 0;
        for(FlowBean value:values){
            upPackNum += value.getUpPackNum();
            downPackNum += value.getDownPackNum();
            upPayLoad += value.getUpPayLoad();
            downPayLoad += value.getDownPayLoad();
        }
        result = new FlowBean(upPackNum,downPackNum,upPayLoad,downPayLoad);
        context.write(key,result);
    }
}
