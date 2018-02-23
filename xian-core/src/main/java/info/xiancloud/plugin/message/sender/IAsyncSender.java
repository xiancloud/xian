package info.xiancloud.plugin.message.sender;

import java.util.concurrent.TimeUnit;

/**
 * This sender is designed to be an async sender, if you need an synchronized one, use {@link SenderFuture#get(long, TimeUnit)} to block your thread and wait for the result.
 *
 * @author happyyangyuan
 */
public interface IAsyncSender {

    /*UnitResponse getOutput();*/

    SenderFuture send();

    /*boolean isCompleted();*/

}
