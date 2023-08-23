package com.freeing.zk;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

/**
 * @author yanggy
 */
public class CuratorTest1 {

    static CuratorFramework client;

    public static void main(String[] args) throws Exception {
        // 重试策略 ExponentialBackoffRetry: 重试一组次数，重试之间的睡眠时间增加
        // RetryNTimes: 重试最大次数; RetryOneTime 只重试一次; RetryUntilElapsed 在给定的时间结束之前重试
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);

        // 创建客户端
        // CuratorFrameworkFactory.newClient()
        client = CuratorFrameworkFactory.builder()
            .connectString("127.0.0.1:2181") // 服务器地址列表，如： host1:port1,host2:port2,host3:port3
            .sessionTimeoutMs(5000) // 会话超时时间
            .connectionTimeoutMs(5000) // 连接超时时间
            .retryPolicy(retryPolicy) // 重试策略，当客户端异常退出或者与服务端失去连接的时候，可以通过设置客户端重新连接 ZooKeeper 服务端
            .namespace("base") // 包含隔离名称 命名空间
            .build();
        client.start();

        testDelete();
    }

    /**
     * 创建节点
     */
    public static void testCreateNode() throws Exception {
        // client.create().forPath("/curator‐node");
        // 待节点属性的，如：持久节点，临时节点，顺序节点等等
        // 可以使用 create 函数创建数据节点，并通过 withMode 函数指定节点类
        // 型（持久化节点，临时节点，顺序节点，临时顺序节点，持久化顺序节点等），默认是持久
        // 化节点，之后调用 forPath 函数来指定节点的路径和数据信息。

        String path = client.create().withMode(CreateMode.PERSISTENT).forPath("/my-node", "somedata".getBytes());
        System.out.println(path);

        // 创建带层级的节点
        String path2 = client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath("/p1/s1/s2");
        System.out.println(path2);
    }

    /**
     * 获取数据
     */
    public static void testGetData() throws Exception {
        byte[] bytes = client.getData().forPath("/my-node");
        System.out.println(new String(bytes));
    }

    /**
     * 更新节点
     */
    public static void testUpdateNode() throws Exception {
        Stat stat = client.setData().forPath("/my-node", "update data".getBytes());

        byte[] bytes = client.getData().forPath("/my-node");
        System.out.println(new String(bytes));
    }

    /**
     * 删除节点
     */
    public static void testDelete() throws Exception {
        // guaranteed：该函数的功能如字面意思一样，主要起到一个保障删除成功的作用，其底层
        // 工作方式是：只要该客户端的会话有效，就会在后台持续发起删除请求，直到该数据节点在
        // ZooKeeper 服务端被删除。
        // deletingChildrenIfNeeded：指定了该函数后，系统在删除该数据节点的时候会以递归的
        // 方式直接删除其子节点，以及子节点的子节点。
        client.delete().guaranteed().deletingChildrenIfNeeded().forPath("/my-node");
    }
}
