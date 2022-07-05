package com.main.bd.hadoop.mr.friends;

import com.main.bd.hadoop.constant.HadoopConf;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
public class FriendsMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(new Configuration(), FriendsMain.class.getSimpleName());
        job.setJarByClass(FriendsMain.class);
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job,new Path(HadoopConf.FILE_PATH+"/friends/in"));
        job.setMapperClass(FriendsMapper.class);
        job.setMapOutputKeyClass(FriendsBean.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(FriendsReducer.class);
        job.setOutputKeyClass(FriendsBean.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job,new Path(HadoopConf.FILE_PATH+"/friends/out"));
        boolean b = job.waitForCompletion(true);
        return b?0:1;
    }
    public static void main(String[] args) throws Exception {
        int run = ToolRunner.run(new Configuration(), new FriendsMain(), args);
        System.exit(run);
    }
}
