package info.xiancloud.plugins.yyq.retry;

import info.xiancloud.core.util.LOG;
import info.xiancloud.core.util.RetryUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * 重试工具类测试
 *
 * @author yyq
 **/
public class RetryTest {

    public static void main(String[] args) throws Exception {

        final Count counts = new Count();
        Future<Integer> result = RetryUtil.retryTask(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                while (counts.count > 1) {
                    counts.count--;
                    throw new Exception("----故意抛出来的");
                }
                LOG.info("---------开始返回结果了");
                return 100;
            }
        }, 2, 3);

        LOG.info("----------------result : " + result.get());
        LOG.info("----------------over");

       /* ExecutorService pool = Executors.newFixedThreadPool(1);

        Future<Integer> future = pool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return 1;
            }
        });

        future.get();*/

   /*     ThreadPoolManager.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("------------run-----------");
            }
        }, 1500);
*/

        /*int result = RetryUtil.retry(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int count = 3;
                while (count > 0) {
                    if (count > 1) {
                        throw new Exception("ddd");
                    } else
                        count--;
                }
                return count;
            }
        }, 2, 3);

        LOG.info("-----result: " + result);*/
    }

    static class Count {
        public static int count = 3;
    }
}


