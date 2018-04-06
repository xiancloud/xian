package info.xiancloud.apidoc.unit.md;

import info.xiancloud.apidoc.handler.filter.FilterByUnits;
import info.xiancloud.apidoc.handler.filter.IUnitFilter;
import info.xiancloud.core.Input;
import info.xiancloud.core.UnitMeta;
import info.xiancloud.core.message.UnitRequest;
import info.xiancloud.core.util.StringUtil;

import java.util.Arrays;

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
        return UnitMeta.createWithDescription("自定义的apidoc生成器").setDataOnly(true);
    }

    @Override
    protected Input otherInput() {
        return new Input()
                .add("unitFilter", String.class, "要生成的unit列表,格式-\"group.unit,group.unit,.....\"", REQUIRED);
    }

    @Override
    protected IUnitFilter getFilter(UnitRequest msg) {
        IUnitFilter filter = new FilterByUnits();
        String unitFilter = msg.getString("unitFilter");
        if (!StringUtil.isEmpty(unitFilter)) {
            String[] fullNameArr = unitFilter.split(",");
            filter.setValues(Arrays.asList(fullNameArr));
        }
        return filter;
    }

}
