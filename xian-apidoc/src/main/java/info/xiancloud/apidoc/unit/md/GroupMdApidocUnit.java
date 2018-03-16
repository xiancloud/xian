package info.xiancloud.apidoc.unit.md;

import info.xiancloud.apidoc.handler.filter.FilterByGroups;
import info.xiancloud.apidoc.handler.filter.IUnitFilter;
import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.message.UnitRequest;

import java.util.ArrayList;

/**
 * apidoc builder for a specified group
 *
 * @author happyyangyuan
 */
public class GroupMdApidocUnit extends AbstractMdApidocUnit {
    @Override
    public String getName() {
        return "groupMd";
    }

    @Override
    public Input otherInput() {
        return Input.create().add("groupName", String.class, "group name", REQUIRED);
    }

    @Override
    protected IUnitFilter getFilter(UnitRequest request) {
        IUnitFilter filter = new FilterByGroups();
        filter.setValues(new ArrayList<String>() {{
            add(request.getString("groupName"));
        }});
        return filter;
    }

}
