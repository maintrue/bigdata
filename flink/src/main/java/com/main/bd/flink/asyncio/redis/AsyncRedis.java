package com.main.bd.flink.asyncio.redis;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 使用异步的方式读取redis的数据
 */
class AsyncRedis extends RichAsyncFunction<String, String> {
    //定义redis的连接池对象
    private JedisPoolConfig config = null;

    private static String ADDR = "localhost";
    private static int PORT = 6379;
    //等待可用连接的最大时间，单位是毫秒，默认是-1，表示永不超时，如果超过等待时间，则会抛出异常
    private static int TIMEOUT = 10000;
    //定义redis的连接池实例
    private JedisPool jedisPool = null;
    //定义连接池的核心对象
    private Jedis jedis = null;

    //初始化redis的连接
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        //定义连接池对象属性配置
        config = new JedisPoolConfig();
        //初始化连接池对象
        jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        //实例化连接对象（获取一个可用的连接）
        jedis = jedisPool.getResource();
    }

    @Override
    public void close() throws Exception {
//        super.close();
//        if (jedis.isConnected()) {
//            try {
//                jedis.close();
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//        }
        this.closeJedis( jedis );
    }

    //异步调用redis
    @Override
    public void asyncInvoke(String input, ResultFuture<String> resultFuture) throws Exception {
        System.out.println("input:" + input);
        //发起一个异步请求，返回结果
        CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                String[] arrayData = input.split(",");
                String name = arrayData[1];
                String value = jedis.hget("AsyncReadRedis", name);
                System.out.println("output:" + value);
                return value;
            }
        }).thenAccept((String dbResult) -> {
            //设置请求完成时的回调，将结果返回
            resultFuture.complete(Collections.singleton(dbResult));
        });
    }

    //连接超时的时候调用的方法，一般在该方法中输出连接超时的错误日志，如果不重新该方法，连接超时后会抛出异常
    @Override
    public void timeout(String input, ResultFuture<String> resultFuture) throws Exception {
        System.out.println("redis connect timeout!");
    }



    // 关闭 jedis
    public void closeJedis( Jedis jedis ) {
        try {
            if ( jedis != null ) {
                if ( jedis != null ) {
                    jedis.close();
                }
            }
        } catch (Exception e) {
            closeBrokenResource( jedis );
        }
    }

    /**
     * Return jedis connection to the pool, call different return methods depends on whether the connection is broken
     */
    public void closeBrokenResource( Jedis jedis ) {
        try {
            jedisPool.returnBrokenResource( jedis );
        } catch ( Exception e ) {
            destroyJedis( jedis );
        }
    }

    /**
     * 在 Jedis Pool 以外强行销毁 Jedis
     */
    public static void destroyJedis( Jedis jedis ) {
        if ( jedis != null ) {
            try {
                jedis.quit();
            } catch ( Exception e ) {
//                print( ">>> RedisUtil-jedis.quit() : " + e );
                e.printStackTrace();
            }

            try {
                jedis.disconnect();
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }
    }
}
