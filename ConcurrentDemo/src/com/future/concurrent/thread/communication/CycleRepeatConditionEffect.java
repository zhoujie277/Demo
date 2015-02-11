/**
 * 
 */
package com.future.concurrent.thread.communication;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 实现三个线程之间的轮循,比如:线程1循环10,线程2循环100,线程3循环20次,然后又是线程1,接着线程2...一直轮循50次.
 * @author JayZhou
 * @date: 2015年2月9日 
 */
public class CycleRepeatConditionEffect {

    private Lock lock = new ReentrantLock();
    private Condition _condition1 = lock.newCondition();
    private Condition _condition2 = lock.newCondition();
    private Condition _condition3 = lock.newCondition();
    private int runningThread = 1;
    private AtomicInteger num = new AtomicInteger();

    private void sub1() {
        try {
            lock.lock();
            while (runningThread != 1) {
                try {
                    _condition1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 10; i++) {
                System.out.print("sub1:" + i + " ");
            }
            System.out.println();
            runningThread = 2;
            _condition2.signal();
        } finally {
            lock.unlock();
        }
    }

    private void sub2() {
        try {
            lock.lock();
            while (runningThread != 2) {
                try {
                    _condition2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 100; i++) {
                System.out.print("sub2:" + i + " ");
            }
            System.out.println();
            runningThread = 3;
            _condition3.signal();
        } finally {
            lock.unlock();
        }
    }

    private void sub3() {
        try {
            lock.lock();
            while (runningThread != 3) {
                try {
                    _condition3.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 20; i++) {
                System.out.print("sub3:" + i + " ");
            }
            System.out.println();
            runningThread = 1;
            _condition1.signal();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        CycleRepeatConditionEffect eff = new CycleRepeatConditionEffect();
        eff.runningThread = 1;
        new Thread(eff.new Sub1Task()).start();
        new Thread(eff.new Sub2Task()).start();
        new Thread(eff.new Sub3Task()).start();
    }

    public class Sub1Task implements Runnable {

        @Override
        public void run() {
            while (num.get() < 50) {
                sub1();
                int get = num.incrementAndGet();
                System.out.println("num:" + get);
            }
        }
    }

    public class Sub2Task implements Runnable {

        @Override
        public void run() {
            while (num.get() < 50) {
                sub2();
            }
        }
    }

    public class Sub3Task implements Runnable {

        @Override
        public void run() {
            while (num.get() < 50) {
                sub3();
            }
        }
    }

}
