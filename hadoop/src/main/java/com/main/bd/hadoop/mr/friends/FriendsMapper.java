package com.main.bd.hadoop.mr.friends;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import java.io.IOException;
public class FriendsMapper extends Mapper<LongWritable, Text,FriendsBean,Text> {
    private FriendsBean keyout = new FriendsBean();
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
        String[] split = value.toString().split(":");
        String mine = split[0];
        String[] friends = (mine+","+split[1]).split(",");
        for(int i=0;i<friends.length;i++){
            for(int j=i+1;j<friends.length;j++){
                String f1 = friends[i];
                String f2 = friends[j];
                if(f1.compareTo(f2) < 0){
                    keyout.setF1(f1);
                    keyout.setF2(f2);
                    context.write(keyout,new Text(mine));
                }else{
                    keyout.setF1(f2);
                    keyout.setF2(f1);
                    context.write(keyout,new Text(mine));
                }
            }
        }
    }
}
