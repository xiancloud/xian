package info.xiancloud.core.message.sender;

import java.util.concurrent.TimeUnit;

/**
 * This sender is designed to be an async sender, if you need an synchronized one, use {@link SenderFuture#get(long, TimeUnit)} to block your thread and wait for the result.
 *
 * @author happyyangyuan
 */
public interface IAsyncSender {

    /*UnitResponse getOutput();*/

    /**
     * send the unit request asynchronously, and return the sender future object.
     */
    SenderFuture send();

    /*boolean isCompleted();*/

}
