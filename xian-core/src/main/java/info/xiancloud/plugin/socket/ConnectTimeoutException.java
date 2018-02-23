package info.xiancloud.plugin.socket;

import java.net.ConnectException;

/**
 * time out for connecting to remote peer.
 *
 * @author happyyangyuan
 */
public class ConnectTimeoutException extends ConnectException {
    public ConnectTimeoutException(String msg) {
        super(msg);
    }
}
