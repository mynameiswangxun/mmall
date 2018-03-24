package com.mmall.util;

import com.mmall.common.RedisShardedPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.ShardedJedis;

@Slf4j
public class RedisShardedPoolUtil {

    public static String set(String key,String value){
        ShardedJedis jedis = null;
        String res = null;

        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.set(key,value);
        } catch (Exception e) {
            log.error("set key:{} value:{} error",key,value,e);
            RedisShardedPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisShardedPool.returnJedis(jedis);
        return res;
    }

    public static String setEx(String key,String value,int exTime){
        ShardedJedis jedis = null;
        String res = null;

        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.setex(key,exTime,value);
        } catch (Exception e) {
            log.error("setex key:{} time:{} value:{} error",key,exTime,value,e);
            RedisShardedPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisShardedPool.returnJedis(jedis);
        return res;
    }

    public static Long expire(String key,int exTime){
        ShardedJedis jedis = null;
        Long res = null;

        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.expire(key,exTime);
        } catch (Exception e) {
            log.error("expire key:{} time:{} error",key,exTime,e);
            RedisShardedPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisShardedPool.returnJedis(jedis);
        return res;
    }

    public static String get(String key){
        ShardedJedis jedis = null;
        String res = null;

        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error",key,e);
            RedisShardedPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisShardedPool.returnJedis(jedis);
        return res;
    }

    public static Long del(String key){
        ShardedJedis jedis = null;
        Long res = null;

        try {
            jedis = RedisShardedPool.getJedis();
            res = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error",key,e);
            RedisShardedPool.returnBorkenJedis(jedis);
            return res;
        }
        RedisShardedPool.returnJedis(jedis);
        return res;
    }

    public static void main(String[] args) {

        RedisShardedPoolUtil.set("key","value");
        String value = RedisShardedPoolUtil.get("key");
        System.out.println(value);
        RedisShardedPoolUtil.setEx("keyex","value",20);
        RedisShardedPoolUtil.expire("key",30);
        RedisShardedPoolUtil.del("wangxun");

        System.out.println("end");
    }

}
