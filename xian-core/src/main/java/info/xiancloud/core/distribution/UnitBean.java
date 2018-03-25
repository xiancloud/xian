package info.xiancloud.core.distribution;

import info.xiancloud.core.Group;
import info.xiancloud.core.Input;
import info.xiancloud.core.Unit;
import info.xiancloud.core.UnitMeta;

/**
 * 1、反序列化远程节点注册过来的unit json对象的
 * 2、描述和缓存全局单例unit的bean，性能考虑
 * <p>
 * 现改为抽象类，请直接使用 {@link UnitProxy}即可
 *
 * @author happyyangyuan
 */
public abstract class UnitBean implements Unit {
    private UnitMeta meta;
    private Input input;
    private String name;
    private GroupBean group;

    public UnitMeta getMeta() {
        return meta;
    }

    public void setMeta(UnitMeta meta) {
        this.meta = meta;
    }

    public Input getInput() {
        return input == null ? new Input() : input;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Group getGroup() {
        return group;
    }

    public void setInput(Input inputObjs) {
        this.input = inputObjs;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGroup(GroupBean group) {
        this.group = group;
    }
}
