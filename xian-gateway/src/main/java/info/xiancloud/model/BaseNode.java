package info.xiancloud.model;

import java.util.Arrays;

/**
 * @deprecated This is used for csv rule configuration, and is not used any more
 */
public class BaseNode {

    private int nodeId;
    private String groupName;
    private String unitName;
    private String[] params;
    private String[] returnParams;
    private BaseNode[] childNodes;
    private int responseCode;

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public BaseNode[] getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(BaseNode[] childNodes) {
        this.childNodes = childNodes;
    }

    public int getNodeId() {
        return nodeId;
    }

    public void setNodeId(int nodeId) {
        this.nodeId = nodeId;
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

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }

    public String[] getReturnParams() {
        return returnParams;
    }

    public void setReturnParams(String[] returnParams) {
        this.returnParams = returnParams;
    }

    @Override
    public String toString() {
        return "BaseNode [nodeId=" + nodeId + ", groupName=" + groupName + ", unitName=" + unitName
                + ", params=" + Arrays.toString(params) + ", returnParams=" + Arrays.toString(returnParams)
                + ", childNodes=" + Arrays.toString(childNodes) + ", responseCode=" + responseCode + "]";
    }


}
