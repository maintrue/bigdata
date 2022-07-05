package com.main.bd.hadoop.mr.counter;

import com.main.bd.hadoop.constant.HadoopConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class CounterMain extends Configured implements Tool {

    @Override
    public int run(String[] strings) throws Exception {
        String home = HadoopConf.FILE_PATH;
        Job job = Job.getInstance(new Configuration(), CounterMain.class.getSimpleName());
        job.setJarByClass(CounterMain.class);
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        TextInputFormat.addInputPath(job,new Path(home+"/counter/in"));
        TextOutputFormat.setOutputPath(job,new Path(home+"/counter/out"));
        job.setMapperClass(CounterMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setReducerClass(CounterReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        boolean b = job.waitForCompletion(true);
        return b?0:1;
    }

    public static void main(String[] args) throws Exception {
        Configuration configuration = new Configuration();
        Tool tool  =  new CounterMain();
        int run = ToolRunner.run(configuration, tool, args);
        System.exit(run);
    }

}
