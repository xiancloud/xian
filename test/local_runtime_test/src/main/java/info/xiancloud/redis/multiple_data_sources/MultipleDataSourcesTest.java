package info.xiancloud.redis.multiple_data_sources;

import info.xiancloud.core.support.cache.CacheConfigBean;
import info.xiancloud.redis.BaseRedis;

import java.net.InetAddress;

public class MultipleDataSourcesTest extends BaseRedis {
    private static final CacheConfigBean CACHE_CONFIG_BEAN = new CacheConfigBean("123.207.53.152:6379", "", getDBIndex());

    public static void main(String[] args) {
        dataSourceName();


//        CacheSetUtil.add("M_D_S_T_S","M_D_S_T_S_0");
//        CacheSetUtil.add("M_D_S_T_S","M_D_S_T_S_1");
//
//        Set<String> setValues = CacheSetUtil.values("M_D_S_T_S");
//        StringBuilder sb = new StringBuilder();
//        for(String value : setValues)
//            sb.append(value).append(", ");
//        System.out.println("M_D_S_T_S: " + sb.toString());
//
//        boolean exists_0 = CacheSetUtil.exists("M_D_S_T_S", "M_D_S_T_S_0");
//        System.out.println("M_D_S_T_S: M_D_S_T_S_0: " + exists_0);
//
//        boolean exists_1 = CacheSetUtil.exists("M_D_S_T_S", "M_D_S_T_S_3");
//        System.out.println("M_D_S_T_S: M_D_S_T_S_3: " + exists_1);


//        CacheObjectUtil.set(CACHE_CONFIG_BEAN, "M_D_S_T_O_0", "M_D_S_T_O_0");
//        String value_0 = CacheObjectUtil.get(CACHE_CONFIG_BEAN, "M_D_S_T_O_0", String.class);
//        System.out.println("M_D_S_T_O_0: " + value_0);
//
//        String value_1 = CacheObjectUtil.get("M_D_S_T_O_0", String.class);
//        System.out.println("M_D_S_T_O_0: " + value_1);
    }

    protected static void dataSourceName() {
        try {
            String host = "nonproduction-qcloud-redis.apaycloud.com";

            InetAddress giriAddress = InetAddress.getByName(host);

            String address = giriAddress.getHostAddress();

            System.out.println(host + " : " + address);
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
        String host_ip = "nonproduction-qcloud-redis.apaycloud.com";
//        String host_ip = "123.207.53.152";

        for (int i = 0; i < 100; i++)
        {
            long startNanoTime = System.nanoTime();

            String ip = Redis.dataSourceName(host_ip);

            long endNanoTime = System.nanoTime();

            System.out.println(i + " : " + ip + " --> " + host_ip + ", 耗时: " + (endNanoTime - startNanoTime) / 1000000);
        }
        */
    }

}
