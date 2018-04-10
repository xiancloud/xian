package info.xiancloud.yy.local_ip;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * @author happyyangyuan
 */
public class TestInet4AdressWithDomainParam {
    public static void main(String[] args) throws IOException {
        System.out.println(Inet4Address.getByName("build-qcloud.apaycloud.com").getHostAddress());
        System.out.println(Inet4Address.getByName("build-qcloud.apaycloud.com").isReachable(1000));;
    }
}
