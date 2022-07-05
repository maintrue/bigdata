package com.main.bd.hadoop.mr.combiner.sort;

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
public class CombinerSortMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(new Configuration(), CombinerSortMain.class.getSimpleName());
        job.setJarByClass(CombinerSortMain.class);
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job, new Path(HadoopConf.FILE_PATH+"/combiner/sort/in"));
        job.setMapperClass(CombinerSortMapper.class);
        job.setMapOutputKeyClass(FlowBean.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(CombinerSortReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(FlowBean.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job, new Path(HadoopConf.FILE_PATH+"/combiner/sort/out"));
        boolean b = job.waitForCompletion(true);
        return b?0:1;
    }
    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new CombinerSortMain(), args);
        System.exit(run);
    }
}
