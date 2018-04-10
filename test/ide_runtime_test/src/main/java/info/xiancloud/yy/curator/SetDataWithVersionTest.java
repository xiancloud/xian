package info.xiancloud.yy.curator;

import info.xiancloud.zookeeper.ZkConnection;
import org.junit.Test;

/**
 * @author happyyangyuan
 */
public class SetDataWithVersionTest {

    @Test
    public void setDataWithVersionTest() throws Exception {
        ZkConnection.start();
        try {
            /*ZkConnection.client.create().creatingParentsIfNeeded().forPath("/YY/11");*/
            ZkConnection.client.setData().withVersion(-1).forPath("/YY/11", "ss".getBytes());
        } finally {
            ZkConnection.close();
        }
    }
}
