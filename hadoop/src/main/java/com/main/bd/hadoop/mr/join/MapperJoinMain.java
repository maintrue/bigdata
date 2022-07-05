package com.main.bd.hadoop.mr.join;

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
import java.net.URI;
public class MapperJoinMain extends Configured implements Tool {
    @Override
    public int run(String[] strings) throws Exception {
        Job job = Job.getInstance(new Configuration(),MapperJoinMain.class.getSimpleName());
        job.setJarByClass(MapperJoinMain.class);
        job.setInputFormatClass(TextInputFormat.class);
        TextInputFormat.addInputPath(job,new Path(HadoopConf.FILE_PATH+ "/join/mapper/in"));
        job.addCacheFile(new URI(HadoopConf.HADOOP_PATH+ "/cache/product.txt"));
        job.setMapperClass(MapperJoinMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        job.setReducerClass(MapperJoinReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);
        job.setOutputFormatClass(TextOutputFormat.class);
        TextOutputFormat.setOutputPath(job,new Path(HadoopConf.FILE_PATH+ "/join/mapper/out"));
        boolean b = job.waitForCompletion(true);
        return b?0:1;
    }
    public static void main(String[] args) throws Exception{
        int run = ToolRunner.run(new Configuration(),new MapperJoinMain(),args);
        System.exit(run);
    }
}
