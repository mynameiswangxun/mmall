package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisPool {
    //jedis 连接池
    private static JedisPool pool;
    //最大连接数
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));
    //在jedisPool中的最大idle(空闲)状态的jedis实例个数
    private static Integer maxIdel = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","10"));
    //在jedisPool中的最小idle(空闲)状态的jedis实例个数
    private static Integer minIdel = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","2"));
    //在borrow一个jedis实例的时候,是否需要进行验证操作,如果为true则borrow到的jedis一定是可用的
    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));
    //在return一个jedis实例的时候,是否需要进行验证操作,如果为true则return的jedis一定是可用的
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","false"));
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort = Integer.parseInt(PropertiesUtil.getProperty("redis.port"));

    private static void initPool(){
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdel);
        config.setMinIdle(minIdel);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        //连接耗尽时是否阻塞直到超时,如果为false连接耗尽不会阻塞而是抛出异常
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config,redisIp,redisPort,1000*2);
    }

    static{
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnJedis(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBorkenJedis(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args){
        Jedis jedis = pool.getResource();
        jedis.set("wangxun","wangxun");
        returnJedis(jedis);

        //销毁连接池中的所有连接
        pool.destroy();
        System.out.println("over");
    }
}
