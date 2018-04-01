package info.xiancloud.ftpclient;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.EnvUtil;
import info.xiancloud.core.util.LOG;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author happyyangyuan
 */
public class SimpleFtpClientUnit implements Unit {
    @Override
    public String getName() {
        return "simpleFtpClient";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("A simple ftp client tool, login every time you use it and the connection is closed after your file uploading is done.")
                .setPublic(false);
    }

    @Override
    public Input getInput() {
        return new Input()
                .add("url", String.class, "ftp url without port", REQUIRED)
                .add("port", int.class, "ftp group port", REQUIRED)
                .add("userName", String.class, "ftp user name", REQUIRED)
                .add("password", String.class, "ftp password", REQUIRED)
                .add("localFilePath", String.class, "local file path, absolute or relative", REQUIRED)
                .add("remotePath", String.class, "The relative path ends with the remote file name. " +
                        "If not provided then the home dir of current ftp user and the local file name are used.", REQUIRED);
    }

    @Override
    public UnitResponse execute(UnitRequest msg) {
        FtpConnection ftpFtpConnection = null;
        try {
            InputStream bis = new FileInputStream(msg.getString("localFilePath"));
            ftpFtpConnection = new FtpConnection(msg.getString("url"), msg.get("port", int.class), msg.getString("userName"), msg.getString("password"));
            ftpFtpConnection.ftp.changeWorkingDirectory(EnvUtil.getEnv());
            ftpFtpConnection.ftp.enterLocalPassiveMode();
            ftpFtpConnection.ftp.storeFile(msg.get("remotePath"), bis);
            return UnitResponse.createSuccess();
        } catch (Exception e) {
            return UnitResponse.createException(e);
        } finally {
            try {
                if (ftpFtpConnection != null && ftpFtpConnection.ftp != null) {
                    ftpFtpConnection.ftp.logout();
                    ftpFtpConnection.ftp.disconnect();
                }
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    @Override
    public Group getGroup() {
        return FtpClientService.singleton;
    }

}
