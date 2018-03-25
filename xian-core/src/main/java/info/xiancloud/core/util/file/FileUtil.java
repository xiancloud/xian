package info.xiancloud.core.util.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author happyyangyuan
 */
public class FileUtil {
    /**
     * 将文件标识为jvm退出时删除文件
     * 注意，如果java虚拟机异常退出，文件是不会被删除的！亲测！
     *
     * @param path 绝对或者相对路径
     */
    public static void deleteOnExit(String path) {
        File file = new File(path);
        file.deleteOnExit();
    }

    /**
     * 删除文件
     *
     * @param path 绝对或者相对路径
     */
    public static void deleteFile(String path) {
        File file = new File(path);
        file.delete();
    }

    /**
     * 拷贝文件，本方法会将输入输出流关闭。
     */
    public static void copyFile(InputStream _inputStream, String newFile) throws IOException {
        try (InputStream inputStream = _inputStream;
             FileOutputStream outputStream = new FileOutputStream(newFile)) {
            int length = 2097152;
            byte[] buffer = new byte[length];
            while (true) {
                int read = inputStream.read(buffer);
                if (read == -1) {
                    outputStream.flush();
                    break;
                } else {
                    outputStream.write(buffer, 0, read);
                }
            }
        }
    }

}
