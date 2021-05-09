package com.lookstarry.doermail.search.thread;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @PackageName:com.lookstarry.doermail.search
 * @NAME:CompletableFutureTest
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/5/4 17:56
 */
public class CompletableFutureTest {
    public static ExecutorService executor = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        System.out.println("main...start...");
//        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//        }, executor);
//        System.out.println("main...end...");

        /**
         * 方法执行完成后的感知
         */
//        System.out.println("main...start...");
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executor).whenComplete((result, exception) -> {
//            // 虽然能得到异常信息，但是没法修改返回数据
//            System.out.println("异步任务成功了...结果是：" + result + "；异常是：" + exception);
//        }).exceptionally(throwable -> {
//            // 可以感知异常，出现异常指定默认返回值
//            return 10;
//        });
//        Integer integer = future.get();
//        System.out.println("main...end..." + integer);

        /**
         * 方法执行完成后的执行，可以感知异常以及上一步的执行结果，有返回值
         */
//        System.out.println("main...start...");
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 0;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executor).handle((result, throwable) -> {
//            if(result != null){
//                return result * 2;
//            }
//            if(throwable != null){
//                return 0;
//            }
//            return 0;
//        });
//        Integer integer = future.get();
//        System.out.println("main...end..." + integer);

        /**
         * 线程串行化
         * 1）thenRun：不能获取到上一步的执行结果，无返回值
         *  .thenRunAsync(() ->{
         *             System.out.println("任务2启动了");
         *         }, executor)
         * 2）thenAccept：可以获取到上一步的返回结果，但是无返回值
         *  .thenAcceptAsync((result) -> {
         *             System.out.println("任务2启动了" + result);
         *         }, executor);
         *
         */
//        System.out.println("main...start...");
//        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("当前线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("运行结果：" + i);
//            return i;
//        }, executor).thenApplyAsync(result -> {
//            System.out.println("任务2启动了" + result);
//            return "hello" + result;
//        }, executor);
//        System.out.println("main...end..." + future.get());

        /**
         * 两个都完成
         */
//        System.out.println("main...start...");
//        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务一线程：" + Thread.currentThread().getId());
//            int i = 10 / 2;
//            System.out.println("任务一结束：");
//            return i;
//        }, executor);
//
//        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
//            System.out.println("任务二线程：" + Thread.currentThread().getId());
//            try {
//                Thread.sleep(3000);
//                System.out.println("任务二结束：");
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return "hello";
//        }, executor);

//        future1.runAfterBothAsync(future2, () -> {
//            System.out.println("任务3开始...");
//        }, executor);
//
//        future1.thenAcceptBothAsync(future2, (result1, result2) -> {
//            System.out.println("任务3开始...任务一结果：" + result1 + ",任务二结果：" + result2);
//        },executor);
//        System.out.println("main...end...");
//
//        CompletableFuture<String> future3 = future1.thenCombineAsync(future2, (result1, result2) -> {
//            return result1 + result2 + "->" + "world";
//        }, executor);

        /**
         * 两个任务只要有一个完成，第三个任务就开始启动
         * runAfterEitherAsync: 不感知结果，自己无返回值
         */
//        future1.runAfterEitherAsync(future2, () -> {
//            System.out.println("任务3开始...之前的结果");
//        }, executor);
//
//        future1.acceptEitherAsync(future2, (result) -> {
//            System.out.println("任务3开始...之前的结果..." + result);
//        }, executor);
//
//        CompletableFuture<String> future3 = future1.applyToEitherAsync(future2, (result) -> {
//            System.out.println("任务3开始...之前的结果" + result.toString());
//            return result.toString() + "哈哈";
//        }, executor);

        CompletableFuture<String> futureImg = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的图片信息");
            return "hello.jpg";
        }, executor);

        CompletableFuture<String> futureAttr = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品的属性");
            return "黑色+256G";
        }, executor);

        CompletableFuture<String> futureDesc = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
                System.out.println("查询商品的介绍");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "华为";
        }, executor);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(futureImg, futureAttr, futureDesc);
        allOf.get(); // 等待所有结果完成
        System.out.println("main...end..." + futureImg.get() + "=>" + futureAttr.get() + "=>" + futureDesc.get());

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(futureImg, futureAttr, futureDesc);
        anyOf.get();
        System.out.println("main...end..." + anyOf.get());
    }
}
