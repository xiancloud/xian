package info.xiancloud.plugin.util.file;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.FileAlreadyExistsException;
import java.util.Properties;

/**
 * 小文本文件操作工具类
 *
 * @author happyyangyuan
 */
public class PlainFileUtil extends FileUtil {

    /**
     * BOM字符,它会出现在文本文件的开头
     */
    public static final String BOM = "\uFEFF";

    /**
     * Read a small text file, and return the whole content.
     * 读取文本文件返回文本文件所有内容
     *
     * @throws RuntimeException file reading failed.
     */
    public static String readAll(File file) throws RuntimeException {
        try (FileInputStream inputStream = new FileInputStream(file)) {
            return IOUtils.toString(inputStream, Charset.forName("utf-8")).trim();
        } catch (IOException io) {
            throw new RuntimeException(io);
        }
    }

    /**
     * create a new named file, and write the content into it.
     * 新建文件,并写文件,文件必须不存在
     *
     * @throws FileAlreadyExistsException FileAlreadyExistsException
     */
    public static File newFile(String path, String content) throws FileAlreadyExistsException {
        File file = new File(path);
        boolean created;
        try {
            created = file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!created) {
            throw new FileAlreadyExistsException("Failed to create'" + path + "'. May be file already exists!");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    /**
     * @param fullPath classpath路径
     * @return 读取classpath中的资源文件, 得到完整内容,注意:它会去除文件开头的BOM特殊字符.
     * @throws IOException
     */
    public static String readClasspathFile(String fullPath) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (
                InputStreamReader reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(fullPath));
                BufferedReader br = new BufferedReader(reader)
        ) {
            br.lines().forEach(line -> sb.append(line).append(System.lineSeparator()));
        }
        return sb.toString().replaceAll(BOM, "");
    }

    /**
     * 注意：1、本方法返回流对象，你使用完毕之后需要手动关闭！切记！
     * 2、如果指定的文件不存在，那么返回的是null
     */
    public static InputStream readClasspathFileIntoStream(String fullPath) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fullPath);
    }

    /**
     * 从classpath内加载指定的文件到{@link Properties}内
     */
    public static Properties readPropertiesFromClasspathFile(String fullPath) {
        Properties properties = new Properties();
        try (InputStream inputStream = readClasspathFileIntoStream(fullPath)) {
            properties.load(new InputStreamReader(inputStream));
        } catch (IOException e) {
            throw new RuntimeException("加载配置文件" + fullPath + "出错");
        }
        return properties;
    }

    /**
     * 从流中读取文件内容，本方法不负责关闭流，请自行关闭
     */
    public static String readFromInputStream(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, Charset.forName("utf-8")).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String... args) throws IOException {
        System.out.println(readPropertiesFromClasspathFile("log4j.properties"));
        File file = new File("/Users/happyyangyuan/settings.xml");
        System.out.println(readAll(file).startsWith(BOM));
        System.out.println(new File("/Users/happyyangyuan/settings.xml").toString());
        System.out.println(new File("/Users/happyyangyuan/settings.xml").toPath());

    }
}
