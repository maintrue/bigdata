package com.main.bd.hadoop.mr.sort;

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
public class SortMain extends Configured implements Tool {
    @Override
   public int run(String[] args) throws Exception {
       Configuration conf = super.getConf();
       Job job = Job.getInstance(conf,SortMain.class.getSimpleName());
       job.setJarByClass(SortMain.class);
       job.setInputFormatClass(TextInputFormat.class);
       TextInputFormat.addInputPath(job,new Path(HadoopConf.FILE_PATH+"/sort/in"));
       TextOutputFormat.setOutputPath(job,new Path(HadoopConf.FILE_PATH+"/sort/out"));
       job.setMapperClass(SortMapper.class);
       job.setMapOutputKeyClass(SortBean.class);
       job.setMapOutputValueClass(NullWritable.class);
       job.setReducerClass(SortReducer.class);
       job.setOutputKeyClass(Text.class);
       job.setOutputValueClass(NullWritable.class);
       boolean b = job.waitForCompletion(true);
       return b?0:1;
   }
    public static void main(String[] args) throws Exception {
        Configuration entries = new Configuration();
        ToolRunner.run(entries,new SortMain(),args);
    }
}
