package info.xiancloud.core.distribution.exception;

import info.xiancloud.core.Group;

import java.util.Set;

/**
 * unit is offline
 */
public class UnitOfflineException extends AbstractXianException {

    private String unitFullName;
    /**
     * todo this is current not supported.
     */
    private Set<String> applications;

    public UnitOfflineException(String unitFullName) {
        this.unitFullName = unitFullName;
        //todo 想办法获取到unit曾经在哪个application内，然后把该信息加入到message提示中，方便业务排查问题。
    }

    @Override
    public String getCode() {
        return Group.CODE_UNIT_OFFLINE;
    }

    @Override
    public String getMessage() {
        return String.format("unit不在线：%s", unitFullName);
    }
}
