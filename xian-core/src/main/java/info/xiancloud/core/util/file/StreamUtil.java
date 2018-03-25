package info.xiancloud.core.util.file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.function.Function;

public class StreamUtil {
    /**
     * 读取流，并且一行一行的处理;读取完毕后会将流关闭。
     *
     * @param inputStream {@link InputStream}
     */
    public static <T> void lineByLine(InputStream inputStream, Function<String, T> function) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                function.apply(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取流，并且一段一段的处理；完毕之后，此方法会帮你把流关闭。
     *
     * @param _inputStream 输入流
     * @param delimiter    分段分隔符
     * @param function     处理函数，入参为一个分段
     */
    public static void partByPart(InputStream _inputStream, String delimiter, Function<String, Object> function) {
        try (Scanner scanner = new Scanner(_inputStream);) {
            scanner.useDelimiter(delimiter);
            while (scanner.hasNext()) {
                String next = scanner.next();
                function.apply(next);
            }
        }
    }


}
