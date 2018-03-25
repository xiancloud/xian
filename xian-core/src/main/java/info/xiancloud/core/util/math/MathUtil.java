package info.xiancloud.core.util.math;

import java.util.List;

/**
 * @author happyyangyuan
 */
public class MathUtil {

    public static int sum(List<Integer> integerList) {
        int sum = 0;
        for (Integer integer : integerList) {
            sum += integer;
        }
        return sum;
    }
}
