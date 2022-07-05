package com.main.bd.hadoop.clickstream.pv;

import com.main.bd.hadoop.clickstream.WebLogBean;
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


/**
 * 
 * 将清洗之后的日志梳理出点击流pageviews模型数据
 * 
 * 输入数据是清洗过后的结果数据
 * 
 * 区分出每一次会话，给每一次visit（session）增加了session-id（随机uuid）
 * 梳理出每一次会话中所访问的每个页面（请求时间，url，停留时长，以及该页面在这次session中的序号）
 * 保留referral_url，body_bytes_send，useragent
 * 
 * 
 * @author
 * 
 */
public class PageViewMain extends Configured implements Tool {

	public static void main(String[] args) throws Exception {

		int run = ToolRunner.run(new Configuration(), new PageViewMain(), args);
		System.exit(run);

	}

	@Override
	public int run(String[] strings) throws Exception {
		Job job = Job.getInstance(super.getConf(),PageViewMain.class.getSimpleName());
		job.setJarByClass(PageViewMain.class);
		job.setInputFormatClass(TextInputFormat.class);
		TextInputFormat.addInputPath(job,new Path(HadoopConf.FILE_PATH+"/offline/pv/in"));
		job.setMapperClass(PageViewMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(WebLogBean.class);
		job.setReducerClass(PageViewReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		TextOutputFormat.setOutputPath(job,new Path(HadoopConf.FILE_PATH+"/offline/pv/out"));
		boolean b = job.waitForCompletion(true);
		return b?0:1;
	}
}
