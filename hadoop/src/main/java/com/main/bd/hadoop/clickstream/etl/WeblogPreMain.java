package com.main.bd.hadoop.clickstream.etl;

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

/**
 * 处理原始日志，过滤出真实pv请求 转换时间格式 对缺失字段填充默认值 对记录标记valid和invalid
 * 
 */

public class WeblogPreMain extends Configured implements Tool {

	public static void main(String[] args) throws Exception {
		int run = ToolRunner.run(new Configuration(), new WeblogPreMain(), args);
		System.exit(run);
	}

	@Override
	public int run(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf);
		job.setJarByClass(WeblogPreMain.class);
		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.addInputPath(job,new Path(HadoopConf.FILE_PATH+"/offline/pre/in"));
		job.setMapperClass(WeblogPreMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(NullWritable.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(NullWritable.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(job,new Path(HadoopConf.FILE_PATH+"/offline/pre/out"));

		job.setNumReduceTasks(0);

		boolean res = job.waitForCompletion(true);
		return res?0:1;
	}
}
