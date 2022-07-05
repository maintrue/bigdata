package com.main.bd.hadoop.mr.join;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
public class MapperJoinMapper extends Mapper<LongWritable, Text,Text,Text> {
    private HashMap<String, String> map = new HashMap<>();
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        URI[] cacheFiles = context.getCacheFiles();
        FileSystem fileSystem = FileSystem.get(cacheFiles[0], context.getConfiguration());
        FSDataInputStream inputStream = fileSystem.open(new Path(cacheFiles[0]));
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = bufferedReader.readLine()) != null){
            map.put(line.split(",")[0],line);
        }
        bufferedReader.close();
        fileSystem.close();
    }
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        //1:从行文本数据中获取商品的id: p0001 , p0002  得到了K2
        String[] split = value.toString().split(",");
        String productId = split[2];  //K2
        //2:在Map集合中,将商品的id作为键,获取值(商品的行文本数据) ,将value和值拼接,得到V2
        String productLine = map.get(productId);
        String valueLine = productLine+"\t"+value.toString(); //V2
        //3:将K2和V2写入上下文中
        context.write(new Text(productId), new Text(valueLine));
    }
}
