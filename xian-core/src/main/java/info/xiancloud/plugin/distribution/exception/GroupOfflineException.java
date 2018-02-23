package info.xiancloud.plugin.distribution.exception;

import info.xiancloud.plugin.Group;

/**
 * Group offline exception
 *
 * @author happyyangyuan at 2018-02-14
 */
public class GroupOfflineException extends AbstractXianException {
    private String service;

    public GroupOfflineException(String service) {
        this.service = service;
    }

    @Override
    public String getCode() {
        return Group.CODE_GROUP_OFFLINE;
    }

    @Override
    public String getMessage() {
        return "group is offline：" + service;
    }


}
