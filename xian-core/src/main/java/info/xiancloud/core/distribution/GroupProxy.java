package info.xiancloud.core.distribution;

import info.xiancloud.core.Group;
import info.xiancloud.core.util.Reflection;

/**
 * 解决Group作为接口类无法存放成员变量的缺陷而设计的代理，其功能与Group实例对象一模一样
 *
 * @author happyyangyuan
 */
public class GroupProxy extends GroupBean {

    private Group group;

    public static GroupProxy create(Group group) {
        GroupProxy groupProxy = Reflection.toType(group, GroupProxy.class);
        groupProxy.group = group;
        return groupProxy;
    }

    /*
    不能定义service对象的getter，否则会被序列化到注册中心内
    public Group getGroupName() {
        return group;
    }*/

    /*private GroupProxy setGroup(Group group) {
        this.group = group;
        return this;
    }*/

}
