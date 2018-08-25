package info.xiancloud.test.reactivepgdao;

import info.xiancloud.core.Group;
import info.xiancloud.core.Handler;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.dao.core.utils.TableMetaCache;

import java.util.concurrent.ExecutionException;

public class TestReactivepgGetColsUnit implements Unit {
    @Override
    public Input getInput() {
        return null;
    }

    @Override
    public Group getGroup() {
        return ReactivepgTestDaoGroup.SINGLETON;
    }

    @Override
    public void execute(UnitRequest request, Handler<UnitResponse> handler) throws ExecutionException {
        handler.handle(UnitResponse.createSuccess(TableMetaCache.COLS.get("untitled_table")));
    }
}
