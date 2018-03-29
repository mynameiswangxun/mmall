package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    //如果正常关闭tomcat会调用该方法，防止V2版本的closeOrder死锁
    @PreDestroy
    public void delLock(){
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
    }

    //@Scheduled(cron = "0 */1 * * * ?")//每个一分钟的整数倍
    public void closeOrderTaskV1(){
        log.info("关闭订单定时任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }
    //@Scheduled(cron = "0 */1 * * * ?")//每个一分钟的整数倍
    public void closeOrderTaskV2(){
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","50000"));

        Long setnexResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnexResult!=null && setnexResult.intValue()==1){
            //如果返回值为1，代表获取锁成功
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            log.info("没有获取到锁");
        }
        log.info("关闭订单定时任务结束");
    }
    @Scheduled(cron = "0 */1 * * * ?")//每个一分钟的整数倍
    public void closeOrderTaskV3(){
        log.info("关闭订单定时任务启动");
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));

        Long setnexResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnexResult!=null && setnexResult.intValue()==1){
            //如果返回值为1，代表获取锁成功
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }else{
            //没有获取到锁，判断当前锁是否过期
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            //如果发现当前锁存在并且已经过期
            if(lockValueStr!=null && System.currentTimeMillis()>Long.parseLong(lockValueStr)){
                //尝试抢占锁
                log.info("发现锁过期，开始抢占锁");
                String getSetRes = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,
                        Long.toString(System.currentTimeMillis()+lockTimeout));
                //情况一：getSetRes==null，锁已被释放，故成功抢占到锁
                //情况二：lockValueStr.equals(getSetRes)，抢占锁之前，锁未被其他进程抢占，故成功抢占到锁
                if(getSetRes==null || lockValueStr.equals(getSetRes)){
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获取到锁，ThreadName:{}",Thread.currentThread().getName());
                }
            }else {
                //如果锁被释放了，就不再继续竞争锁了，已经竞争过一次
                //或者锁存在并且尚未过期
                log.info("没有获取到锁，ThreadName:{}",Thread.currentThread().getName());
            }
        }
        log.info("关闭订单定时任务结束");
    }
    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,5);//有效期5秒
        log.info("获取{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{}，ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("===================================================");
    }
}
