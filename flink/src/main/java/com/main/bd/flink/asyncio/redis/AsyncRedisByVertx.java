package com.main.bd.flink.asyncio.redis;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Collections;

/**
 * 使用高性能异步组件vertx实现类似于连接池的功能，效率比连接池要高
 * 1）在java版本中可以直接使用
 * 2）如果在scala版本中使用的话，需要scala的版本是2.12+
 */
class AsyncRedisByVertx extends RichAsyncFunction<String, String> {
    //用transient关键字标记的成员变量不参与序列化过程
    private transient RedisClient redisClient;
    //获取连接池的配置对象
    private JedisPoolConfig config = null;
    //获取连接池
    JedisPool jedisPool = null;
    //获取核心对象
    Jedis jedis = null;
    //Redis服务器IP
    private static String ADDR = "127.0.0.1";
    //Redis的端口号
    private static int PORT = 6379;
    //访问密码
    private static String AUTH = "XXXXXX";
    //等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(AsyncRedis.class);

    //初始化连接
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        config = new JedisPoolConfig();
        jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
        jedis = jedisPool.getResource();

        RedisOptions config = new RedisOptions();
        config.setHost(ADDR);
        config.setPort(PORT);

        VertxOptions vo = new VertxOptions();
        vo.setEventLoopPoolSize(10);
        vo.setWorkerPoolSize(20);

        Vertx vertx = Vertx.vertx(vo);

        redisClient = RedisClient.create(vertx, config);
    }

    //数据异步调用
    @Override
    public void asyncInvoke(String input, ResultFuture<String> resultFuture) throws Exception {
        System.out.println("input:" + input);
        String[] split = input.split(",");
        String name = split[1];
        // 发起一个异步请求
        redisClient.hget("AsyncReadRedis", name, res -> {
            if (res.succeeded()) {
                String result = res.result();
                if (result == null) {
                    resultFuture.complete(null);
                    return;
                } else {
                    // 设置请求完成时的回调: 将结果传递给 collector
                    resultFuture.complete(Collections.singleton(result));
                }
            } else if (res.failed()) {
                resultFuture.complete(null);
                return;
            }
        });
    }

    @Override
    public void timeout(String input, ResultFuture resultFuture) throws Exception {
    }

    @Override
    public void close() throws Exception {
        super.close();
        if (redisClient != null) {
            redisClient.close(null);
        }
    }
}
