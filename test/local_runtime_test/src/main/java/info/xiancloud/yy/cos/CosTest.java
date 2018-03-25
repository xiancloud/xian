package info.xiancloud.yy.cos;

import info.xiancloud.core.support.cos.CloudFile;

/**
 * @author happyyangyuan
 */
public class CosTest {
    public static void main(String[] args) {
        System.out.println(CloudFile.exists("yy/99.txt"));
    }
}
