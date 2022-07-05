package com.main.bd.hadoop.mr.combiner.partitioner;

import com.main.bd.hadoop.constant.HadoopConf;
import com.main.bd.hadoop.mr.combiner.FlowBean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class CombinerParitionerMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(new Configuration(), CombinerParitionerMain.class.getSimpleName());
        job.setJarByClass(CombinerParitionerMain.class);
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(HadoopConf.FILE_PATH+"/combiner/partitioner/in"));
        job.setMapperClass(CombinerPartitionerMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(FlowBean.class);
        // 设置分区规则
        job.setPartitionerClass(CombinerPatitioner.class);
        // 设置同等的分区数
        job.setNumReduceTasks(4);
        job.setReducerClass(CombinerPartitionerReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(HadoopConf.FILE_PATH+"/combiner/partitioner/out"));
        boolean b = job.waitForCompletion(true);
        return b?0:1;
    }
    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new CombinerParitionerMain(), args);
        System.exit(run);
    }
}
