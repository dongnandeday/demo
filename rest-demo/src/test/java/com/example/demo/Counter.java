package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yangkun
 *         <p>测试基于自旋CAS原子操作的线程安全计数器方法和普通非线程安全计数器方法</p>
 *         generate on 16/5/5
 */
public class Counter {
    private AtomicInteger atomicI = new AtomicInteger(0);
    private int i = 0;
    private volatile int vi = 0;

    public static void main(String[] args) {
        final Counter cas = new Counter();
        List<Thread> ts = new ArrayList<>(600);
        long start = System.currentTimeMillis();
        for(int j = 0; j < 100; j++) {
            Thread t = new Thread(() -> {
                for(int i1 = 0; i1 < 10000; i1++) {
                    cas.count();
                    cas.safeCount();
                    cas.vcount();
                }
            });
            ts.add(t);
        }
        ts.forEach(Thread::start);

        // 将所有线程join到main线程中,防止mian方法结束时计算线程还未结束
        for(Thread t : ts) {
            try {
                t.join();
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(cas.i);
        System.out.println(cas.atomicI.get());
        System.out.println(cas.vi);
        System.out.println(String.format("Time: %d ms", System.currentTimeMillis() - start));
    }

    private void safeCount() {
        for(; ; ) {
            int i = atomicI.get();
            boolean suc = atomicI.compareAndSet(i, ++i);
            if(suc) {
                break;
            }
        }
    }

    private void count() {
        i++;
    }

    private void vcount() {
        vi++;
    }
}
