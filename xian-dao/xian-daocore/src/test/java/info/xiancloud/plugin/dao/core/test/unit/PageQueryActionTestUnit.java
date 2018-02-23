package info.xiancloud.plugin.dao.core.test.unit;

import info.xiancloud.plugin.Group;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.dao.core.group.unit.DaoUnit;
import info.xiancloud.plugin.dao.core.jdbc.sql.Action;
import info.xiancloud.plugin.dao.core.jdbc.sql.PaginateSelectAction;
import info.xiancloud.plugin.dao.core.test.DaoTestGroup;

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
        return UnitMeta.create("PageQueryActionTest").setPublic(false);
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
