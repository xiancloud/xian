package info.xiancloud.plugin.message.sender.virtureunit;

import info.xiancloud.plugin.distribution.UnitJudge;
import info.xiancloud.plugin.util.LOG;
import info.xiancloud.plugin.util.StringUtil;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * dao虚拟unit名转实体unit名，本类为无状态，建议使用单例模式
 *
 * @author happyyangyuan
 */
public class VirtualDaoUnitConverter implements IVirtualUnitConverter {

    public static final IVirtualUnitConverter singleton = new VirtualDaoUnitConverter();

    private String buildBaseUnit(String unitName, Map<String, Object> map) {
        if (!StringUtil.isEmpty(unitName)) {
            LOG.debug(">>>>>>>尝试查找DbBaseUnit：" + unitName);
            String baseUnitName = null, tableName = null;
            Pattern p = Pattern.compile("^(add|delete|query|update|paginate)(.+)(ByIdDB)$");
            Matcher m = p.matcher(unitName);
            if (m.find()) {
                baseUnitName = "Base" + StringUtil.firstCharToUpperCase(m.group(1)) + m.group(3);
                tableName = StringUtil.firstCharToLowerCase(m.group(2));
            } else {
                p = Pattern.compile("^(add|delete|query|paginate)(.+)(DB)$");
                //p = Pattern.compile("^(add|delete|query|update|paginate)(.+)(DB)$");//updateDB还没实现
                m = p.matcher(unitName);
                if (m.find()) {
                    baseUnitName = "Base" + StringUtil.firstCharToUpperCase(m.group(1)) + m.group(3);
                    tableName = StringUtil.firstCharToLowerCase(m.group(2));
                }
            }
            if (!StringUtil.isEmpty(baseUnitName) && !StringUtil.isEmpty(tableName)) {
                map.put("$tableName", tableName);
                LOG.debug(">>>>>>>找到baseUnit:" + baseUnitName);
                return baseUnitName;
            }
        }
        LOG.debug("不匹配base dao Unit");
        return null;
    }

    @Override
    public String getConcreteUnit(String daoGroupName, String daoUnitName, Map<String, Object> map) {
        String baseUnitName;
        if (!UnitJudge.defined(daoGroupName, daoUnitName)) {
            //only when unit is undefined
            if ((baseUnitName = buildBaseUnit(daoUnitName, map)) != null) {
                return baseUnitName;
            }
        }
        return daoUnitName;
    }
}
