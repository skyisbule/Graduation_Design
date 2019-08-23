package com.github.skyisbule.db.lock;

/**
 * 这个包下面的类主要是用来做事务的锁处理
 * 对数据进行加锁操作，只有申请到了锁的事务才可以进行
 */
public interface Locker {

    public boolean doLock(Integer transactionId,String key,Integer min,Integer max);

    public boolean doRelease(Integer transactionId,String key,Integer min,Integer max);

}
