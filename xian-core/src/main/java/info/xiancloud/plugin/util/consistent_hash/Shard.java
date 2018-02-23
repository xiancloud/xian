package info.xiancloud.plugin.util.consistent_hash;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import info.xiancloud.plugin.message.IdManager;
import info.xiancloud.plugin.util.ArrayUtil;
import info.xiancloud.plugin.util.RandomUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * @author happyyangyuan
 */
public class Shard<S> { // S类封装了机器节点的信息 ，如name、password、ip、port等

    private TreeMap<Long, S> nodes; // 虚拟节点
    private List<S> shards; // 真实机器节点
    private final int NODE_NUM = 100; // 每个机器节点关联的虚拟节点个数

    /**
     * Create a Shard instance.
     * Please Always keep the same oder of the node list or you will lose the constancy!
     *
     * @param shards the node list, note that this list must be kept the same order in each shard creation or you will
     *               lose the constancy.
     */
    public Shard(List<S> shards) {
        super();
        this.shards = shards;
        init();
    }

    private void init() { // 初始化一致性hash环
        nodes = new TreeMap<Long, S>();
        for (int i = 0; i != shards.size(); ++i) { // 每个真实机器节点都需要关联虚拟节点
            final S shardInfo = shards.get(i);

            for (int n = 0; n < NODE_NUM; n++)
                // 一个真实机器节点关联NODE_NUM个虚拟节点
                nodes.put(hash("SHARD-" + i + "-NODE-" + n), shardInfo);

        }
    }

    public S getShardInfo(String key) {
        SortedMap<Long, S> tail = nodes.tailMap(hash(key)); // 沿环的顺时针找到一个虚拟节点
        if (tail.size() == 0) {
            return nodes.get(nodes.firstKey());
        }
        return tail.get(tail.firstKey()); // 返回该虚拟节点对应的真实机器节点的信息
    }

    /**
     * MurMurHash算法，是非加密HASH算法，性能很高，
     * 比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免）
     * 等HASH算法要快很多，而且据说这个算法的碰撞率很低.
     * http://murmurhash.googlepages.com/
     */
    private Long hash(String key) {

        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(
                    ByteOrder.LITTLE_ENDIAN);
            // for big-endian version, do this first:
            // finish.position(8-buf.remaining());
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }

    public static void main(String[] args) {
        System.out.println("================================性能测试==========================================================");
        long start = System.currentTimeMillis();
        final int MAC_COUNT = 1000000;
        Multimap<String, String> map1 = ArrayListMultimap.create();
        List<String> nodeList = new ArrayList<String>() {{
            add(IdManager.generateStaticQueueId("yy-0"));
            add(IdManager.generateStaticQueueId("yy-1"));
            add(IdManager.generateStaticQueueId("yy-2"));
        }};
        Shard<String> shard1 = new Shard<>(nodeList);
        for (int i = 0; i < MAC_COUNT; i++) {
            String mac = RandomUtils.getRandomNumbers(11);
            map1.put(shard1.getShardInfo(mac), mac);
        }
        System.out.println(MAC_COUNT + "个MAC分布到" + nodeList.size() + "个节点耗时:" + (System.currentTimeMillis() - start) + "ms");
        System.out.println(map1.keys());
        System.out.println("===============================结束==========================================================");


        System.out.println("================================一致性测试==========================================================");
        List<String> macs = generateMacs(MAC_COUNT);
        List<Multimap<String, String>> maps = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Multimap<String, String> mapN = ArrayListMultimap.create();
            Shard<String> shardN = new Shard<>(new ArrayList<String>() {{
                add(IdManager.generateStaticQueueId("yy-0"));
                add(IdManager.generateStaticQueueId("yy-1"));
                add(IdManager.generateStaticQueueId("yy-2"));
                add(IdManager.generateStaticQueueId("yy-3"));
                add(IdManager.generateStaticQueueId("yy-4"));
                add(IdManager.generateStaticQueueId("yy-5"));
            }});
            for (String mac : macs) {
                mapN.put(shardN.getShardInfo(mac), mac);
            }
            maps.add(mapN);
        }
        Iterator<String> it = maps.get(0).keySet().iterator();
        while (it.hasNext()) {
            String clientId = it.next();
            for (int i = 1; i < maps.size(); i++) {
                Multimap<String, String> map0 = maps.get(i - 1);
                Multimap<String, String> map = maps.get(i);
                List<String> macs0 = (List<String>) map0.get(clientId);
                List<String> macs1 = (List<String>) map.get(clientId);
                List<String> intersection = ArrayUtil.getIntersection(macs1, macs0);
                if (macs1.size() != macs0.size() || intersection.size() != macs1.size()) {
                    System.out.println("intersection.size  :" + intersection);
                    System.out.println("macs.size : " + macs1);
                    System.out.println("macs0.size : " + macs0);
                    new Throwable("不相等").printStackTrace();
                }
            }
        }
        System.out.println("===============================结束==========================================================");


        {
            Shard<String> shard = new Shard<>(new ArrayList<String>() {{
                add("1");
                add("3");
            }});
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(shard.getShardInfo(i + ""), i);
            }
            System.out.println(map);
        }
        {
            Shard<String> shard = new Shard<>(new ArrayList<String>() {{
                add("1");
                add("3");
                add("4");
            }});
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(shard.getShardInfo(i + ""), i);
            }
            System.out.println(map);
        }
    }

    private static List<String> generateMacs(final int MAC_COUNT) {
        List<String> macs = new ArrayList<>();
        for (int j = 0; j < MAC_COUNT; j++) {
            String mac = RandomUtils.getRandomNumbers(11);
            macs.add(mac);
        }
        return macs;
    }

}

