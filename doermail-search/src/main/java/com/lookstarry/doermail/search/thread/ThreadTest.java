package com.lookstarry.doermail.search.thread;

import java.util.concurrent.*;

/**
 * @PackageName:com.lookstarry.doermail.search
 * @NAME:ThreadTest
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/4 09:30
 */
public class ThreadTest {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        /**
         * 1) 继承Thread
         *         Thread01 thread01 = new Thread01();
         *         thread01.start(); // 启动线程
         * 2) 实现Runnable接口
         *         Runable01 runable01 = new Runable01();
         *         new Thread(runable01).start();
         * 3）实现Callable接口 + FutureTask（可以拿到返回结果，可以处理异常）
         *         FutureTask<Integer> integerFutureTask = new FutureTask<>(new Callable01());
         *         new Thread(integerFutureTask).start();
         *         // 阻塞等待整个线程执行，获取返回结果
         *         Integer integer = integerFutureTask.get();
         * 4）线程池
         *      给线程池直接提交任务
         *      创建 1）Executors 2) new ThreadPoolExecutor
         *      阻塞队列new LinkedBlockingDeque<>() 默认为Integer的最大值，注意数量否则内存不够
         *
         */
        new ThreadPoolExecutor(5,
                200,
                10,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
//        Executors.newFixedThreadPool(10); // 核心线程、最大线程都是固定为10，都不可回收
//        Executors.newCachedThreadPool(); // 核心线程数为0，所有都可回收
//        Executors.newScheduledThreadPool();  // 定时任务的线程池
//        Executors.newSingleThreadExecutor(); // 核心线程、最大线程数都为1，后台从队列中获取任务挨个执行

    }

    public static class Thread01 extends Thread{
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    public static class Runable01 implements Runnable{
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }

    // 泛型接收返回值
    public static class Callable01 implements Callable<Integer>{
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }
}
