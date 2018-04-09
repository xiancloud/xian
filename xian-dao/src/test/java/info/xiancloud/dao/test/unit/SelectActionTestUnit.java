package info.xiancloud.dao.test.unit;

import info.xiancloud.core.Group;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.dao.group.unit.DaoUnit;
import info.xiancloud.dao.jdbc.sql.Action;
import info.xiancloud.dao.jdbc.sql.SelectAction;
import info.xiancloud.dao.test.DaoTestGroup;

/**
 * @author happyyangyuan
 */
public class SelectActionTestUnit extends DaoUnit {
    @Override
    public String getName() {
        return "SelectActionTestUnit";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.createWithDescription("SelectActionTestUnit").setDocApi(false);
    }

    @Override
    public Action[] getActions() {
        return new Action[]{new SelectAction() {
            @Override
            public String[] select() {
                return new String[0];
            }

            @Override
            public String[] fromTable() {
                return new String[]{"ucs_user u left join acc_order_log_201511 o on u.user_id = o.user_id left join device_info d on u.user_id = d.user_id"};
            }

            @Override
            protected String[] where() {
                return new String[]{
//                        "u.user_id = {userId}",
//                        "u.user_name={userName}",
                        "u.status in {status}"
//                        "u.phone_num in {phone_nums}"
                };
            }
        }};
    }

    @Override
    public Group getGroup() {
        return DaoTestGroup.singleton;
    }

}
