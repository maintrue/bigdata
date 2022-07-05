package com.main.bd.hadoop.clickstream.uv;

import com.main.bd.hadoop.clickstream.PageViewsBean;
import com.main.bd.hadoop.clickstream.UserVisitBean;
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
 * 输入数据：pageviews模型结果数据
 * 从pageviews模型结果数据中进一步梳理出visit模型
 * sessionid  start-time   out-time   start-page   out-page   pagecounts  ......
 * 
 * @author
 *
 */
public class UserVisitMain extends Configured implements Tool {

	public static void main(String[] args) throws Exception {

		int run = ToolRunner.run(new Configuration(), new UserVisitMain(), args);
		System.exit(run);

	}

	@Override
	public int run(String[] strings) throws Exception {
		Job job = Job.getInstance(super.getConf(),UserVisitMain.class.getSimpleName());
		job.setJarByClass(UserVisitMain.class);

		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.addInputPath(job,new Path(HadoopConf.FILE_PATH+"/offline/uv/in"));
		job.setMapperClass(UserVisitMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(PageViewsBean.class);
		job.setReducerClass(UserVisitReducer.class);
		job.setOutputKeyClass(NullWritable.class);
		job.setOutputValueClass(UserVisitBean.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(job,new Path(HadoopConf.FILE_PATH+"/offline/uv/out"));

		boolean b = job.waitForCompletion(true);
		return b?0:1;
	}
}
