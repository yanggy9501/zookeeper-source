package com.freeing.zk;

import org.apache.zookeeper.*;

import java.io.IOException;

/**
 * zk 客服端测试
 *
 * @author yanggy
 */
public class ZkClientTest implements Watcher {
    private static  ZooKeeper zk = null;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, new ZkClientTest());
        // 操作：写数据
        zk.create("/path1/path2", "test create".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            if (Event.EventType.None == watchedEvent.getType() && watchedEvent.getPath() == null) {

            }
        }
    }
}
