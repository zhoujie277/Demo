/**
 * 
 */
package com.future.concurrent.thread.communication;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 子线程循环10次，接着主线程循环100，接着又回到子线程循环10次，接着再回到主线程又循环100，如此循环50次，请写出程序。
 * 该程序的缺点：用synchronized，没有区分读写锁，性能不佳.
 * 或者暴露了isSubRunning，让其在多线程环境下访问.(注释)
 * 另外：每个循环都新开了一个线程，浪费资源(考虑用阻塞队列)
 * @author JayZhou
 * @date: 2015年2月9日 
 */
public class CycleRepeatReadWriteLock {

    private Object objLock = new Object();
    private volatile boolean isSubRunning = true;

    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public static void main(String[] args) {
        CycleRepeatReadWriteLock demo = new CycleRepeatReadWriteLock();
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
        // if(isSubRunning){
        synchronized (objLock) {
            if (isSubRunning) {
                try {
                    objLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // }
        for (int i = 0; i < 100; i++) {
            System.out.print("cycleHundred: " + i + " ");
        }
        System.out.println();
        synchronized (objLock) {
            isSubRunning = true;
            objLock.notifyAll();
        }
    }

    /**
     * 子线程循环10次
     */
    public void cycleTen() {
        // if(!isSubRunning){
        synchronized (objLock) {
            if (!isSubRunning) {
                try {
                    objLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // }
        for (int i = 0; i < 10; i++) {
            System.out.print("cycleTen: " + i + " ");
        }
        System.out.println();
        synchronized (objLock) {
            isSubRunning = false;
            objLock.notifyAll();
        }
    }

    public class SubThread implements Runnable {
        @Override
        public void run() {
            cycleTen();
        }
    }

}
