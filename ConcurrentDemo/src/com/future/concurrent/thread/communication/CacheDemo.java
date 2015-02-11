/**
 * 
 */
package com.future.concurrent.thread.communication;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 详见Java API ReentrantReadWriteLock 经典缓存用法.
 * 缓存数据
 * @author JayZhou
 * @date: 2015年2月11日 
 */
public class CacheDemo {

    private HashMap<String, Object> cache = new HashMap<String, Object>();

    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * 多线程环境，则会引起系统不正确.
     * 可能cache连着存了一样的值.
     * @param key
     * @return
     */
    public Object getCache(String key) {
        if (key == null || key.trim().length() == 0) {
            return null;
        }
        Object object = cache.get(key);
        if (object == null) {
            object = new Object();
            cache.put(key, object);
        }
        return object;
    }

    /**
     * 多线程环境,更安全
     * @param key
     * @return
     */
    public Object getCache2(String key) {
        if (key == null || key.trim().length() == 0) {
            return null;
        }
        try {
            lock.readLock().lock();
            Object object = cache.get(key);
            if (object == null) {
                lock.readLock().unlock();
                try {
                    lock.writeLock().lock();
                    // 假设三个线程同时去获取写锁,我们知道只有第一个线程能够获取
                    // 那么其他两个线程只有等了,如果第一个线程按流程执行完后,刚才的两个线程可以得到写锁了,
                    // 然后接着就可以修改数据了(赋值).所以加上判断!
                    if (object == null) {
                        object = new Object();
                        cache.put(key, object);
                    }
                    // 降级,通过释放写锁之前获取读锁
                    lock.readLock().lock();
                } finally {
                    lock.writeLock().unlock();
                }
            }
            return object;
        } finally {
            lock.readLock().unlock();
        }
    }
}
