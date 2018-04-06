package info.xiancloud.dao.test.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.PaginateSelectAction;
import info.xiancloud.dao.test.DaoTestGroup;

/**
 * @author happyyangyuan
 */
public class PageQueryActionTestUnit extends DaoUnit {
    @Override
    public String getName() {
        return "PageQueryActionTest";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("PageQueryActionTest").setPublic(false);
    }

    @Override
    public Action[] getActions() {
        return new Action[]{new PaginateSelectAction() {
            @Override
            public String[] select() {
                return new String[0];
            }

            @Override
            public String[] fromTable() {
                return new String[]{"ucs_user u"};
            }

            @Override
            protected String[] where() {
                return new String[]{"u.user_id = {userId}",
                        "or u.user_name={userName}"};
            }
        }};
    }

    @Override
    public Group getGroup() {
        return DaoTestGroup.singleton;
    }

}
