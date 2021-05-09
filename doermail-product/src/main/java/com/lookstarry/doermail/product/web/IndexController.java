package com.lookstarry.doermail.product.web;

import com.lookstarry.doermail.product.entity.CategoryEntity;
import com.lookstarry.doermail.product.service.CategoryService;
import com.lookstarry.doermail.product.vo.Catelog2Vo;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @PackageName:com.lookstarry.doermail.product.web
 * @NAME:IndexController
 * @Description:
 * @author: yizhichangyuan
 * @date:2021/4/17 20:02
 */
@Controller
public class IndexController {
    @Autowired
    CategoryService categoryService;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping({"/", "/index.html", "/index"})
    public String indexPage(Model model){
        // TODO 查出所有的一级分类，展示在页面中
        List<CategoryEntity> categoryEntities = categoryService.getLevel1Categorys();

        // 视图解析器进行拼串
        // thymeleaf.prefix = "classpath:/templates/"
        // thymeleaf.suffix = ".html"
        model.addAttribute("categorys", categoryEntities);
        return "index";
    }

    // index/catalog.json
    @ResponseBody
    @GetMapping("/index/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatalogJson(){
        Map<String, List<Catelog2Vo>> map = categoryService.getCatalogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String getHello(){
        // 1.获取同一把锁，只要锁的名字一样，就是同一把锁
        RLock lock = redisson.getLock("my-lock");

        // 2.加锁，lock.lock()
        // 1)、锁的自动续期，如果业务超长，运行期间会自动给锁续上新的30s，不用担心业务时间超长，锁自动过期被删掉
        // 2)、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认在30s以后自动删除
//        lock.lock(); // 没有获取到锁的会在while(true)中不断尝试获取锁（默认加的锁过期时间为30s）


//        lock.lock(10, TimeUnit.SECONDS); // 自己设置锁过期时间，锁到点后会自动删除，而不会自动续期，所以如果业务超时即可能被其他人抢占，所以要求锁的过期时间一定要大于业务的执行时间
        // 1.如果我们传递了锁的过期时间，就发送给redis执行脚本，进行占锁，默认超时就是我们指定的时间
        // 2.如果我们未指定锁的过期时间，就使用30 * 1000 【LockWatchdogTimeout看门狗的默认时间】
        // 只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】
        // 续期的时间点：每过三分之一的看门狗时间10s就会自动续期，续成30s this.internalLockLeaseTime / 3L

        // 最佳实战，还是指定好超时时间超过业务操作时间，尽管没有看门狗，如果超时说明业务也完蛋了
        lock.lock(30, TimeUnit.SECONDS);
        try{
            System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (InterruptedException e) {

        } finally {
            // 3.解锁
            lock.unlock();
            System.out.println("释放锁..." + Thread.currentThread().getId());
        }

        return "hello";
    }

    // 写锁写数据时未释放，读锁必须等待，修改期间，写锁是一个排他锁（互斥锁、独享锁），读锁是一个共享锁
    // 读 + 读：相当于无锁，并发读，只会在redis中记录号，所有当前的读锁，他们都会同时加锁成功
    // 写 + 读：读必须等待写锁释放
    // 写 + 写：阻塞方式
    // 读 + 写：写锁阻塞必须等待
    @ResponseBody
    @GetMapping("/write")
    public String writeValue(){
        // 模拟一个请求来写数据，加写锁
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = lock.writeLock();
        String s = "";
        try{
            // 1.该数据加写锁，读数据加读锁
            rLock.lock();
            System.out.println("写锁加锁成功..." + Thread.currentThread().getId());
            s = UUID.randomUUID().toString();
            Thread.sleep(30000);
            redisTemplate.opsForValue().set("writeValue", s);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("写锁释放..." + Thread.currentThread().getId());
        }
        return s;
    }

    // 读写锁可以保证读锁始终读取到最新的数据
    @ResponseBody
    @GetMapping("/read")
    public String readValue(){
        // 模拟一个请求来读数据，加读锁
        RReadWriteLock lock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = lock.readLock();
        String s = "";
        try{
            rLock.lock();
            System.out.println("读锁加锁成功..." + Thread.currentThread().getId());
            s = redisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("读锁释放..." + Thread.currentThread().getId());

        }
        return s;
    }

    /**
     * 车库停车：3车位
     * 信号量也可以用在分布式限流
     */
    @ResponseBody
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
//        park.acquire(); // 获取一个信号，获取一个值，占一个车位，没有车位可剩，就会一直阻塞等待
        boolean b = park.tryAcquire(); // 尝试来获取，获取不了就算了，可以用在限流，只允许最多10000个请求进入
        return "ok=>" + b;
    }

    @ResponseBody
    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release(); // 释放一个车位
        return "ok";
    }

    /**
     * 例如学校放假锁大门，例如假设学校只有五个班级
     * 只有当5个班全部走完，我们可以锁大门
     */
    @ResponseBody
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5); // 设置好几个班级
        door.await(); // 等待闭锁都完成，等待door编程0
        return "放假了...";
    }

    @ResponseBody
    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown(); // 计数减1
        return id + "班的人都走了...";
    }

}
