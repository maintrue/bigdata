package com.main.bd.hadoop.hdfs;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class HdfsApi {

    public static String HADOOP_HOME = "hdfs://192.168.163.140:8020";

    /**
     * 使用url方式访问数据（了解）
     * @throws Exception
     */
    @Test
    public void demo1()throws  Exception{
        //第一步：注册hdfs 的url
        URL.setURLStreamHandlerFactory(new FsUrlStreamHandlerFactory());
        //获取文件输入流
        InputStream inputStream  = new URL(HADOOP_HOME+"/a/b/c/NOTICE.txt").openStream();
        //获取文件输出流
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\hello.txt"));
        //实现文件的拷贝
        IOUtils.copy(inputStream, outputStream);
        //关闭流
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
    }

    /**
     * 获取 FileSystem 的第一种方式
     * @throws IOException
     */
    @Test
    public void getFileSystem1() throws IOException {
        Configuration configuration = new Configuration();
        //指定我们使用的文件系统类型:
        configuration.set("fs.defaultFS", HADOOP_HOME+"/");
        //获取指定的文件系统
        FileSystem fileSystem = FileSystem.get(configuration);
        System.out.println(fileSystem.toString());
    }

    /**
     *
     * 获取 FileSystem 的第二种方式
     * @throws IOException
     */
    @Test
    public void getFileSystem2() throws  Exception{
        Configuration conf = new Configuration();
        URI uri = new URI(HADOOP_HOME);
        FileSystem fileSystem = FileSystem.get(uri, conf);
        System.out.println(fileSystem);
    }

    /**
     * 获取 FileSystem 的第三种方式
     * @throws IOException
     */
    @Test
    public void getFileSystem3() throws  Exception{
        Configuration configuration = new Configuration();
        configuration.set("fs.defaultFS", HADOOP_HOME);
        FileSystem fileSystem = FileSystem.newInstance(configuration);
        System.out.println(fileSystem.toString());
    }

    /**
     * 获取 FileSystem 的第四种方式
     * @throws IOException
     */
    @Test
    public void getFileSystem4() throws  Exception{
        Configuration conf = new Configuration();
        URI uri = new URI(HADOOP_HOME);
        FileSystem fileSystem = FileSystem.newInstance(uri, conf);
        System.out.println(fileSystem.toString());
    }

    /**
     * 遍历 HDFS 中所有文件
     * @throws Exception
     */
    @Test
    public void listMyFiles()throws Exception{
        //获取fileSystem类
        FileSystem fileSystem = FileSystem.get(new URI(HADOOP_HOME),new Configuration());
        //获取RemoteIterator 得到所有的文件或者文件夹，第一个参数指定遍历的路径，第二个参数表示是否要递归遍历
        RemoteIterator<LocatedFileStatus> locatedFileStatusRemoteIterator = fileSystem.listFiles(new Path("/"), true);
        while (locatedFileStatusRemoteIterator.hasNext()){
            LocatedFileStatus next = locatedFileStatusRemoteIterator.next();
            System.out.println(next.getPath().toString());
        }
        fileSystem.close();
    }

    /**
     * HDFS 上创建文件夹
     * @throws Exception
     */
    @Test
    public void mkdirs() throws Exception{
        FileSystem fileSystem = FileSystem.get(new URI(HADOOP_HOME),new Configuration());
        boolean mkdirs = fileSystem.mkdirs(new Path("/hello/mydir/test"));
        fileSystem.close();
    }

    /**
     * 下载文件
     * @throws Exception
     */
    @Test
    public void getFileToLocal()throws  Exception{
        FileSystem fileSystem = FileSystem.get(new URI(HADOOP_HOME),new Configuration());
        FSDataInputStream inputStream = fileSystem.open(new Path("/wordcount_out"));
        FileOutputStream outputStream = new FileOutputStream(new File("D:\\student.csv"));
        IOUtils.copy(inputStream,outputStream );
        IOUtils.closeQuietly(inputStream);
        IOUtils.closeQuietly(outputStream);
        fileSystem.close();
    }

    /**
     * HDFS 文件上传
     * @throws Exception
     */
    @Test
    public void putData() throws  Exception{
        FileSystem fileSystem = FileSystem.get(new URI(HADOOP_HOME),new Configuration());
        fileSystem.copyFromLocalFile(new Path("file:///D:\\student.csv"),new Path("/hello/mydir/test"));
        fileSystem.close();
    }


}
