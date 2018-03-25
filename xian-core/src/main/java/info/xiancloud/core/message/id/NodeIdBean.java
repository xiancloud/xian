package info.xiancloud.core.message.id;

import info.xiancloud.core.util.StringUtil;

import java.io.Serializable;

/**
 * nodeId解析器的bean
 * 理论上，请不要轻易修改或扩展nodeId的定义；如果你非要修改或者扩展节点id的结构，请关注此类.
 *
 * @author happyyangyuan
 */
public class NodeIdBean implements Serializable {
    private String env;//第一位
    private String application; //第二位,即systemEnumName
    private String processName;//第三位
    public static final String splitter = "---";
    private String clientId;

    public NodeIdBean(String env, String application, String processName) {
        if (StringUtil.isEmpty(env) || StringUtil.isEmpty(application) || StringUtil.isEmpty(processName)) {
            throw new RuntimeException(String.format("参数不允许为空:env = %s , application=%s , processName = %s", env, application, processName));
        }
        this.env = env;
        this.application = application;
        this.processName = processName;
        clientId = env.concat(splitter).concat(application).concat(splitter).concat(processName);
    }

    public static NodeIdBean parse(String nodeId) {
        String[] split = nodeId.split(StringUtil.escapeSpecialChar(splitter));
        return new NodeIdBean(split[0], split[1], split[2]);
    }

    public String getClientId() {
        return clientId;
    }

    public String getEnv() {
        return env;
    }

    public String getApplication() {
        return application;
    }

    public String getHostname() {
        return processName.split("@")[1];
    }

    public String getProcessName() {
        return processName;
    }

}
