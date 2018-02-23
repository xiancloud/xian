package info.xiancloud.plugin.scheduler;

import info.xiancloud.plugin.message.UnitResponse;

/**
 * @author happyyangyuan
 */
public interface IResponseExtractor {
    String extractContext(UnitResponse unitResponse);
}
