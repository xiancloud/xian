package info.xiancloud.plugin.ftp.client;

import info.xiancloud.plugin.Group;

/**
 * @author happyyangyuan
 */
public class FtpClientService implements Group {
    public static final Group singleton = new FtpClientService();

    @Override
    public String getName() {
        return "ftpClientService";
    }

}
