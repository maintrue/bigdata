package com.main.bd.hadoop.mr.file.input;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import java.io.IOException;
public class UnionFileRecordReader  extends RecordReader<NullWritable, BytesWritable> {
    private Configuration configuration = null;
    private FileSplit fileSplit = null;
    private BytesWritable bytesWritable = new BytesWritable();
    private boolean processed = false;
    FileSystem fileSystem = null;
    FSDataInputStream inputStream = null;
    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        configuration = taskAttemptContext.getConfiguration();
        fileSplit = (FileSplit) inputSplit;
    }
    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if(!processed){
            fileSystem = FileSystem.get(configuration);
            inputStream = fileSystem.open(fileSplit.getPath());
            int length = (int) fileSplit.getLength();
            byte[] bytes = new byte[length];
            IOUtils.read(inputStream,bytes);
            bytesWritable.set(bytes,0,length);
            processed = true;
            return true;
        }
        return false;
    }
    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        return NullWritable.get();
    }
    @Override
    public BytesWritable getCurrentValue() throws IOException, InterruptedException {
        return bytesWritable;
    }
    @Override
    public float getProgress() throws IOException, InterruptedException {
        return 0;
    }
    @Override
    public void close() throws IOException {
        inputStream.close();
        fileSystem.close();
    }
}
