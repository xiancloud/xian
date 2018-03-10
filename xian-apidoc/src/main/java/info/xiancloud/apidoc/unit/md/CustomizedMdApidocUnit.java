package info.xiancloud.apidoc.unit.md;

import info.xiancloud.plugin.Input;
import info.xiancloud.plugin.UnitMeta;
import info.xiancloud.plugin.message.UnitRequest;
import info.xiancloud.plugin.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * custom api doc generator unit.
 *
 * @author yyq, happyyangyuan
 */
public class CustomizedMdApidocUnit extends AbstractMdApidocUnit {

    @Override
    public String getName() {
        return "customizedMd";
    }

    @Override
    public UnitMeta getMeta() {
        return UnitMeta.create("自定义的apidoc生成器").setDataOnly(true);
    }

    @Override
    protected Input otherInput() {
        return new Input()
                .add("unitFilter", String.class, "要生成的unit列表,格式-\"group.unit,group.unit,.....\"", REQUIRED);
    }

    @Override
    protected Map<String, List<String>> filter(UnitRequest msg) {
        String unitFilter = msg.getString("unitFilter");
        Map<String, List<String>> filterMap = null;
        if (!StringUtil.isEmpty(unitFilter)) {
            filterMap = new HashMap<>();
            String[] fullNameArr = unitFilter.split(",");
            for (String fullName : fullNameArr) {
                String[] sb = fullName.split("\\.");
                String groupName = sb[0];
                String unitName = sb[1];
                List<String> unitList = filterMap.computeIfAbsent(groupName, k -> new ArrayList<>());
                unitList.add(unitName);
            }
        }
        return filterMap;
    }

}
