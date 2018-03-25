package info.xiancloud.gateway.scheduler;

import info.xiancloud.core.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public interface IResponseExtractor {
    String extractContext(UnitResponse unitResponse);
}
