package info.xiancloud.yy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author happyyangyuan
 */
public class TestReflectionInvocationPerformance {

    private static Method method;
    private static MyMethod singleton = new MyMethod();

    static {
        try {
            method = MyMethod.class.getMethod("someMethod", int.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static class MyMethod {
        public String someMethod(int callStackDepth) {
            return "xxx" + "yyy" + callStackDepth;
        }
    }

    /**
     * Test all four methods
     */
    public static void main(String[] args) {
        testMethod(() -> {
            try {
                method.invoke(singleton, 1);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }, "reflectionInvocation");
        testMethod(() -> singleton.someMethod(1), "directCall");
    }

    private static void testMethod(Runnable runnable, String title) {
        long startTime = System.nanoTime();
        for (int i = 0; i < 100000000; i++) {
            runnable.run();
        }
        printElapsedTime(title, startTime);
    }

    private static void printElapsedTime(String title, long startTime) {
        System.out.println(title + ": " + ((double) (System.nanoTime() - startTime)) / 1000000 + " ms.");
    }
}
