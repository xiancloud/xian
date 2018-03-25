package info.xiancloud.core.distribution.service_discovery;


import info.xiancloud.core.distribution.UnitProxy;

/**
 * 分布式服务注册和发现中的unit实例bean
 *
 * @author happyyangyuan
 */
public class UnitInstance extends Instance<UnitProxy> {

    private UnitInstanceIdBean unitInstanceIdBean;

    public String getNodeId() {
        return unitInstanceIdBean.getNodeId();
    }

    public String getFullName() {
        return unitInstanceIdBean.getFullName();
    }

    public void setId(String id) {
        super.setId(id);
        this.unitInstanceIdBean = new UnitInstanceIdBean(id);
    }

    public void setUnitInstanceIdBean(UnitInstanceIdBean unitInstanceIdBean) {
        super.setId(unitInstanceIdBean.getUnitInstanceId());
        this.unitInstanceIdBean = unitInstanceIdBean;
    }

}
