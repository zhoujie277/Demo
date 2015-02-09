/**
 * 
 */
package com.future.concurrent.thread.communication;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 子线程循环10次，接着主线程循环100，接着又回到子线程循环10次，接着再回到主线程又循环100，如此循环50次，请写出程序。
 * 该程序的缺点：
 * 1. 用统一的锁与Synchronized一样，没有区分读写锁，性能不佳. 或者暴露了isSubRunning，让其在多线程环境下访问.(注释)
 * 2. 每个循环都新开了一个线程，浪费资源(考虑用阻塞队列)
 * @author JayZhou
 * @date: 2015年2月9日 
 */
public class CycleRepeatReentrantLock {

    private volatile boolean isSubRunning = true;

    private Lock lock = new ReentrantLock();
    private Condition _condition = lock.newCondition();

    public static void main(String[] args) {
        CycleRepeatReentrantLock demo = new CycleRepeatReentrantLock();
        SubThread subThread = demo.new SubThread();
        for (int i = 0; i < 50; i++) {
            new Thread(subThread).start();
            demo.cycleHundred();
        }
    }

    /**
     * 主线程循环100次
     */
    public void cycleHundred() {
        // 缺点：每次进入都会被锁住
        try {
            lock.lock();
            while (isSubRunning) {
                try {
                    _condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 100; i++) {
                System.out.print("cycleHundred: " + i + " ");
            }
            System.out.println();
            isSubRunning = true;
            _condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 子线程循环10次
     */
    public void cycleTen() {
        // 缺点：每次进入都会被锁住
        try {
            lock.lock();
            while (!isSubRunning) {
                try {
                    _condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 10; i++) {
                System.out.print("cycleTen: " + i + " ");
            }
            System.out.println();
            isSubRunning = false;
            _condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public class SubThread implements Runnable {
        @Override
        public void run() {
            cycleTen();
        }
    }

}
