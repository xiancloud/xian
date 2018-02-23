package info.xiancloud.plugin.server;

import info.xiancloud.plugin.util.Reflection;

import java.util.List;

/**
 * Interface for the  gateway (http) server provider to implement.
 * The api gateway server plugin must implement this interface to receive the business response event.
 *
 * @author happyyangyuan
 */
public interface IServerResponder {

    List<IServerResponder> GATEWAY_RESPONSES = Reflection.getSubClassInstances(IServerResponder.class);
    IServerResponder singleton = GATEWAY_RESPONSES.isEmpty() ? null : GATEWAY_RESPONSES.get(0);

    void response(ServerResponseBean responseBean);

}
