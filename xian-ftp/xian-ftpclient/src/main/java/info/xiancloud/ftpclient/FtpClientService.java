package info.xiancloud.ftpclient;

import info.xiancloud.core.Group;

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
