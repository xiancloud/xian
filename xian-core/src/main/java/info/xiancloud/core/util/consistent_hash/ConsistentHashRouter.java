package info.xiancloud.core.util.consistent_hash;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import info.xiancloud.core.util.JavaPIDUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.google.common.hash.Hashing.consistentHash;

/**
 * @author songwenjun
 * @deprecated 未经过严格测试，请谨慎使用
 */
public class ConsistentHashRouter {
    private SortedMap<Long, VirtualNode> ring = new TreeMap<Long, VirtualNode>();
    private MD5Hash hashfunction = new MD5Hash();

    public ConsistentHashRouter(Collection<PhysicalNode> pNodes, int vnodeCount) {
        for (PhysicalNode pNode : pNodes) {
            addNode(pNode, vnodeCount);
        }
    }

    public void addNode(PhysicalNode pNode, int vnodeCount) {
        int existingReplicas = getReplicas(pNode.toString());
        for (int i = 0; i < vnodeCount; i++) {
            VirtualNode vNode = new VirtualNode(pNode, i + existingReplicas);
            ring.put(hashfunction.hash(vNode.toString()), vNode);
        }
    }

    public void removeNode(PhysicalNode pNode) {
        Iterator<Long> it = ring.keySet().iterator();
        while (it.hasNext()) {
            Long key = it.next();
            VirtualNode virtualNode = ring.get(key);
            if (virtualNode.matches(pNode.toString())) {
                it.remove();
            }
        }
    }

    public PhysicalNode getNode(String key) {
        if (ring.isEmpty()) {
            return null;
        }
        Long hashKey = hashfunction.hash(key);
        SortedMap<Long, VirtualNode> tailMap = ring.tailMap(hashKey);
        hashKey = tailMap != null && !tailMap.isEmpty() ? tailMap.firstKey() : ring.firstKey();
        return ring.get(hashKey).getParent();
    }

    public int getReplicas(String nodeName) {
        int replicas = 0;
        for (VirtualNode node : ring.values()) {
            if (node.matches(nodeName)) {
                replicas++;
            }
        }
        return replicas;
    }

    private static class MD5Hash {
        MessageDigest instance;

        public MD5Hash() {
            try {
                instance = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
            }
        }

        long hash(String key) {
            instance.reset();
            instance.update(key.getBytes());
            byte[] digest = instance.digest();

            long h = 0;
            for (int i = 0; i < 4; i++) {
                h <<= 8;
                h |= ((int) digest[i]) & 0xFF;
            }
            return h;
        }
    }

    public static void main(String[] args) {

        PhysicalNode physicalNode_0 = new PhysicalNode("yy-0", JavaPIDUtil.getHostname(), 1);
        PhysicalNode physicalNode_1 = new PhysicalNode("yy-1", JavaPIDUtil.getHostname(), 1);
        PhysicalNode physicalNode_2 = new PhysicalNode("yy-2", JavaPIDUtil.getHostname(), 1);
        PhysicalNode physicalNode_3 = new PhysicalNode("yy-3", JavaPIDUtil.getHostname(), 1);
        PhysicalNode physicalNode_4 = new PhysicalNode("yy-4", JavaPIDUtil.getHostname(), 1);
        PhysicalNode physicalNode_5 = new PhysicalNode("yy-5", JavaPIDUtil.getHostname(), 1);
        /*for (int j = 0; j < 20; j++) {
            {
                ConsistentHashRouter router = new ConsistentHashRouter(new ArrayList<PhysicalNode>() {{
                    add(physicalNode_0);
                    add(physicalNode_1);
                    add(physicalNode_2);
                    add(physicalNode_3);
                    add(physicalNode_4);
                }}, 1);
                Multimap map = ArrayListMultimap.create();
                for (int i = 0; i < 500; i++) {
                    map.put(router.getNode(i + "").getDomain(), i);
                }
                System.out.println(map);
            }
        }*/
        {
            ConsistentHashRouter router = new ConsistentHashRouter(new ArrayList<PhysicalNode>() {{
                add(physicalNode_0);
                add(physicalNode_1);
                add(physicalNode_2);
                add(physicalNode_3);
                add(physicalNode_4);
            }}, 10);
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(router.getNode(i + "").getDomain(), i);
            }
            System.out.println(map);
        }
        {
            ConsistentHashRouter router = new ConsistentHashRouter(new ArrayList<PhysicalNode>() {{
                add(physicalNode_0);
                add(physicalNode_1);
                add(physicalNode_2);
                add(physicalNode_4);
            }}, 10);
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(router.getNode(i + "").getDomain(), i);
            }
            System.out.println(map);
        }
        {
            ConsistentHashRouter router = new ConsistentHashRouter(new ArrayList<PhysicalNode>() {{
                add(physicalNode_0);
                add(physicalNode_1);
                add(physicalNode_2);
                add(physicalNode_4);
                add(physicalNode_5);
            }}, 10);
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(router.getNode(i + "").getDomain(), i);
            }
            System.out.println(map);
        }


        int bucket = 5;
        {
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(consistentHash(i, bucket), i);
            }
            System.out.println(bucket + ":" + map);
        }
        bucket = 3;
        {
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(consistentHash(i, bucket), i);
            }
            System.out.println(bucket + ":" + map);
        }
        bucket = 4;
        {
            Multimap map = ArrayListMultimap.create();
            for (int i = 0; i < 20; i++) {
                map.put(consistentHash(i, bucket), i);
            }
            System.out.println(bucket + ":" + map);
        }
    }

}
