package info.xiancloud.core.distribution;

import info.xiancloud.core.Group;
import info.xiancloud.core.LocalUnitsManager;
import info.xiancloud.core.Unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Bean description class for group. This class exists because {@link Group interface group}  can not hold member values.
 *
 * @author happyyangyuan
 */
public class GroupBean implements Group {

    private String name;
    private String description;
    private List<String> unitNames;
    private boolean dao;// whether this group bean represents a dao group

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return group在本地节点下的unit名称列表视图
     */
    public List<String> getUnitNames() {
        if (unitNames == null) {
            unitNames = new ArrayList<>();
            LocalUnitsManager.unitMap(untMap -> {
                untMap.forEach((group, unitList) -> {
                    for (Unit unit : unitList) {
                        if (Objects.equals(group, getName()))
                            unitNames.add(unit.getName());
                    }
                });
            });
        }
        return unitNames;
    }

    public void setUnitNames(List<String> unitNames) {
        this.unitNames = unitNames;
    }

    @Override
    public boolean isDao() {
        return dao;
    }

    public void setDao(boolean dao) {
        this.dao = dao;
    }
}
