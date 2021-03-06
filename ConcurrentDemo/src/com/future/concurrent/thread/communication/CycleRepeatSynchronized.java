/**
 * 
 */
package com.future.concurrent.thread.communication;

/**
 * 子线程循环10次，接着主线程循环100，接着又回到子线程循环10次，接着再回到主线程又循环100，如此循环50次，请写出程序。
 * 该程序的缺点：
 * 1. 用synchronized，没有区分读写锁，性能不佳. 或者暴露了isSubRunning，让其在多线程环境下访问.(注释)
 * 2. 每个循环都新开了一个线程，浪费资源(考虑用阻塞队列)
 * 3. for循环没有包括在锁内，虽然在本程序不会出错，但是封装的不够好，出现在多线程程序中容易出错.
 * @author JayZhou
 * @date: 2015年2月9日 
 */
public class CycleRepeatSynchronized {

    private Object objLock = new Object();
    private volatile boolean isSubRunning = true;

    public static void main(String[] args) {
        CycleRepeatSynchronized demo = new CycleRepeatSynchronized();
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
        // if(isSubRunning){
        synchronized (objLock) {
            while (isSubRunning) {
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
        // 缺点：每次进入都会被锁住
        // if(!isSubRunning){
        synchronized (objLock) {
            while (!isSubRunning) {
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
