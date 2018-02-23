package info.xiancloud.plugin.log4j1;

import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 附加历史日志删除功能
 *
 * @author happyyangyuan
 */
public class CustomDailyRollingFileAppender extends DailyRollingFileAppender {

    /**
     * log4j.appender.logfile.maxBackupIndex
     */
    private int maxBackupIndex = 1;

    /**
     * 下次检查删除历史日志的时间,初始值是系统启动当天的开始时间，即当天凌晨。
     * 这样可保证系统启动时先做一遍日志滚动检查。
     */
    private long nextCheckForDeletion = initNextCheckForDeletion();

    private long initNextCheckForDeletion() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public static void main(String... args) {
        Date d = new Date(new CustomDailyRollingFileAppender().initNextCheckForDeletion());
        System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(d));
    }

    @Override
    protected void subAppend(LoggingEvent event) {
        super.subAppend(event);
        if (System.currentTimeMillis() > nextCheckForDeletion) {
            nextCheckForDeletion = nextCheckForDeletion + 24 * 60 * 60 * 1000;//后延一天
            delete();
        }
    }

    private void delete() {
        List<ModifiedTimeSortableFile> files = getAllFiles();
        Collections.sort(files);
        if (files.size() >= maxBackupIndex) {
            int index = 0;
            int diff = files.size() - maxBackupIndex;
            for (ModifiedTimeSortableFile file : files) {
                if (index >= diff)
                    break;
                System.out.println("  [log4j]清理磁盘空间,删除较为久远的日志文件:" + file.getAbsolutePath());
                file.delete();
                index++;
            }
        }
    }

    public int getMaxBackupIndex() {
        return maxBackupIndex;
    }

    public void setMaxBackupIndex(int maxBackups) {
        this.maxBackupIndex = maxBackups;
    }

    private List<ModifiedTimeSortableFile> getAllFiles() {
        List<ModifiedTimeSortableFile> files = new ArrayList<ModifiedTimeSortableFile>();
        FilenameFilter filter = (dir, simpleFileName) -> {
            try {
                String directoryName = dir.getCanonicalPath();
                String file = new File(fileName).getCanonicalPath();
                String localFile = file.substring(directoryName.length() + 1);
                return simpleFileName.startsWith(localFile);
            } catch (IOException e) {
                LogLog.error("", e);
                return false;
            }
        };
        File file = new File(fileName);
        String parentDirectory = file.getParent();
        if (file.exists()) {
            if (file.getParent() == null) {
                String absolutePath = file.getAbsolutePath();
                parentDirectory = absolutePath.substring(0, absolutePath.lastIndexOf(fileName));
            }
        }
        File dir = new File(parentDirectory);
        String[] names = dir.list(filter);

        for (int i = 0; i < names.length; i++) {
            files.add(new ModifiedTimeSortableFile
                    (dir + System.getProperty("file.separator") + names[i]));
        }
        return files;
    }

    //支持按修改日志排序文件
    private class ModifiedTimeSortableFile extends File implements Serializable, Comparable<File> {
        private static final long serialVersionUID = 1373373728209668895L;

        public ModifiedTimeSortableFile(String parent, String child) {
            super(parent, child);
        }

        public ModifiedTimeSortableFile(URI uri) {
            super(uri);
        }

        public ModifiedTimeSortableFile(File parent, String child) {
            super(parent, child);
        }

        public ModifiedTimeSortableFile(String string) {
            super(string);
        }

        public int compareTo(File anotherPathName) {
            long thisVal = this.lastModified();
            long anotherVal = anotherPathName.lastModified();
            return (thisVal < anotherVal ? -1 : (thisVal == anotherVal ? 0 : 1));
        }
    }
}
