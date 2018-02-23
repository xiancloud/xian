package info.xiancloud.yy;

import info.xiancloud.plugin.conf.plugin.PluginConfig;

import java.util.Arrays;

/**
 * 测试envConfig性能
 */
public class TestEnvConfigPerformance {

    private static void basicTest() {
        System.out.println(Arrays.toString(PluginConfig.performanceTestGetStringArrayUsingStackTrace("performanceTestKey")));
        System.out.println(Arrays.toString(PluginConfig.performanceTestGetStringArrayUsingSunReflection("performanceTestKey")));

        final int repeatN = 1000000;
        long start = System.nanoTime();
        for (int i = 0; i < repeatN; i++) {
            PluginConfig.performanceTestGetStringArrayUsingSunReflection("performanceTestKey");
        }
        System.out.println("performanceTestGetConfigUsingSunReflection cost:   " + (System.nanoTime() - start) / 1000000F + " ms");

        start = System.nanoTime();
        for (int i = 0; i < repeatN; i++) {
            PluginConfig.performanceTestGetStringArrayUsingStackTrace("performanceTestKey");
        }
        System.out.println("performanceTestGetStringArrayUsingStackTrace cost:   " + (System.nanoTime() - start) / 1000000F + " ms");
    }


    public static void main(String... args) {
        recursive(10);
    }

    //制造深层堆栈
    private static void recursive(int depth) {
        if (depth > 0) {
            recursive(--depth);
        } else
            basicTest();
    }
}
