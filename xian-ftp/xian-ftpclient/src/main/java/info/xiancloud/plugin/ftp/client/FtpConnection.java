package info.xiancloud.plugin.ftp.client;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

/**
 * Created by zgc on 2017/4/25.
 */
class FtpConnection {

    FTPClient ftp;

    private static String WORKDIR = "";

    FtpConnection(String url, int port, String userName, String password) throws Exception {
        ftp = connect(WORKDIR, url, port, userName, password);
    }

    FTPClient connect(String path, String addr, int port, String username, String password) throws Exception {
        FTPClient ftp = new FTPClient();
        int reply;
        ftp.connect(addr, port);
        ftp.login(username, password);
        ftp.configure(new FTPClientConfig(ftp.getSystemType()));
        ftp.setControlEncoding("UTF-8");
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        reply = ftp.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            ftp.disconnect();
            return null;
        }
        ftp.changeWorkingDirectory(path);
        return ftp;
    }
}
