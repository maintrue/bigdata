package com.main.bd.hadoop.mr.friends;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import java.io.IOException;
public class FriendsReducer extends Reducer<FriendsBean,Text,FriendsBean,Text> {
    @Override
    protected void reduce(FriendsBean key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String sameFriends = "";
        for(Text value:values){
            if(!key.toString().contains(value.toString())){
                sameFriends += value;
            }
        }
        if(!"".equals(sameFriends)){
            context.write(key,new Text(sameFriends));
        }
    }
}
