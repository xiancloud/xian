package info.xiancloud.core.distribution.exception;

import info.xiancloud.core.Group;
import info.xiancloud.core.message.UnitResponse;
import info.xiancloud.core.util.StringUtil;

/**
 * unit is not defined.
 *
 * @author happyyangyuan
 */
public class UnitUndefinedException extends AbstractXianException {
    private String groupName;
    private String unitName;

    public UnitUndefinedException(String groupName, String unitName) {
        this.groupName = groupName;
        this.unitName = unitName;
    }

    public UnitUndefinedException(String fullName) {
        this.groupName = fullName.split(StringUtil.escapeSpecialChar("."))[0];
        this.unitName = fullName.split(StringUtil.escapeSpecialChar("."))[1];
    }

    @Override
    public String getMessage() {
        return String.format("Unit is undefined: %s.%s", groupName, unitName);
    }

    @Override
    public UnitResponse toUnitResponse() {
        return UnitResponse.createError(Group.CODE_UNIT_UNDEFINED, null, getLocalizedMessage());
    }

    @Override
    public String getCode() {
        return Group.CODE_UNIT_UNDEFINED;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }
}
