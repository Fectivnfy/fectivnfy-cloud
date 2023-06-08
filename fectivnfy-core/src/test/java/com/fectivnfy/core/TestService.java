package com.fectivnfy.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TestService {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    //控制子线程是否能提交
    private static volatile Boolean IS_OK = Boolean.TRUE;

    //子线程状态
    private static final List<Boolean> childStatus = new ArrayList<>();

    public static void push() {
        CountDownLatch childCount = new CountDownLatch(2);
        CountDownLatch minCount = new CountDownLatch(1);

        try {
            Future<Long> addGoods = addGoods(childCount, minCount);
            Future<Long> addArticle = addArticle(childCount, minCount);

            childCount.await();
            //判断子线程状态决定提交回滚
            for (Boolean status : childStatus) {
                if (!status) {
                    System.out.println("主线程：检测到存在线程失败，正在通知其他线程回滚");
                    IS_OK = false;
                    break;
                }
            }
            Long goodId = addGoods.get();
            Long article = addArticle.get();
            System.out.println("新增成功的商品id:" + goodId);
            System.out.println("新增成功的资讯id:" + article);
        } catch (Exception e) {
            System.out.println("主线程：主线程执行失败，正在通知其他线程回滚");
            IS_OK = false;
        }


        //通知
        minCount.countDown();
    }

    public static Future<Long> addGoods(CountDownLatch child, CountDownLatch main) {
        System.out.println("商品线程：新增商品开启线程");
        return executorService.submit(() -> {
            try {
                System.out.println("商品线程：正在新增商品");
                Thread.sleep(100);
                childStatus.add(true);
                //标识子线程完成任务
                child.countDown();
                //执行完成后进入等待
                main.await();

                if (IS_OK) {
                    System.out.println("商品线程：所有线程执行正常，正在提交商品线程事务");
                } else {
                    System.out.println("商品线程：存在失败的线程，正在回滚商品线程事务");
                }

            } catch (Exception e) {
                childStatus.add(false);
                child.countDown();
                System.out.println("商品线程：新增商品异常正在回滚");
            }
            return 666L;
        });
    }

    public static Future<Long> addArticle(CountDownLatch child, CountDownLatch main) {
        System.out.println("资讯线程：新增资讯开启线程");
        return executorService.submit(() -> {
            try {
                System.out.println("资讯线程：正在新增资讯");
                Thread.sleep(100);
                childStatus.add(true);
                //标识子线程完成任务
                child.countDown();
                //制造异常
                buildException();
                //执行完成后进入等待
                main.await();

                if (IS_OK) {
                    System.out.println("资讯线程：所有线程执行正常，正在提交商品线程事务");
                } else {
                    System.out.println("资讯线程：存在失败的线程，正在回滚商品线程事务");
                }

            } catch (Exception e) {
                childStatus.add(false);
                child.countDown();
                System.out.println("资讯线程：新增资讯异常正在回滚");
            }
            return 777L;
        });
    }

    private static void buildException() {
        throw new NullPointerException();
    }
}
