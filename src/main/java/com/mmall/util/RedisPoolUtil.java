package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

@Slf4j
public class RedisPoolUtil {
    public static String set(String key,String value){
        Jedis jedis = null;
        String res = null;

        try {
            jedis = RedisPool.getJedis();
            res = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisPool.returnJedis(jedis);
        return res;
    }

    public static String setEx(String key,String value,int exTime){
        Jedis jedis = null;
        String res = null;

        try {
            jedis = RedisPool.getJedis();
            res = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} time:{} value:{} error",key,exTime,value,e);
            RedisPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisPool.returnJedis(jedis);
        return res;
    }

    public static Long expire(String key,int exTime){
        Jedis jedis = null;
        Long res = null;

        try {
            jedis = RedisPool.getJedis();
            res = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} time:{} error",key,exTime,e);
            RedisPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisPool.returnJedis(jedis);
        return res;
    }

    public static String get(String key){
        Jedis jedis = null;
        String res = null;

        try {
            jedis = RedisPool.getJedis();
            res = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            RedisPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisPool.returnJedis(jedis);
        return res;
    }

    public static Long del(String key){
        Jedis jedis = null;
        Long res = null;

        try {
            jedis = RedisPool.getJedis();
            res = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error",key,e);
            RedisPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisPool.returnJedis(jedis);
        return res;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();

        RedisShardedPoolUtil.set("key","value");
        String value = RedisShardedPoolUtil.get("key");
        RedisShardedPoolUtil.setEx("keyex","value",20);
        RedisShardedPoolUtil.expire("key",30);
        RedisShardedPoolUtil.del("wangxun");

        System.out.println("end");
    }
}
