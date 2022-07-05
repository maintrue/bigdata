package com.main.bd.hadoop.mr.combiner.sort;

import com.main.bd.hadoop.mr.combiner.FlowBean;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
public class CombinerSortReducer extends Reducer<FlowBean, Text, Text,FlowBean> {
    @Override
    protected void reduce(FlowBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        context.write(values.iterator().next(), key);
    }
}
