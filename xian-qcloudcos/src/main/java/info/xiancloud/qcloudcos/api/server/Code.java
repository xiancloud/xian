package info.xiancloud.qcloudcos.api.server;

/**
 * 返回码
 * @author yyq
 *
 */
public enum Code {

	SUCCESS("0",""),
	ParamEmpty("1000","业务参数不能为空"),
	NeedBuctName("1001","需要传递buctName"),
	NeedCosPath("1002","需要传递存储路径cosPath"),
	NeedOp("1003","需要传递操作类型op"),
	NOOP("1004","不支持的操作"),
	BuctNameConfig("1005","bucket对应的host没有配置在config.txt文件中"),
	Fail("-1","操作失败");
	
	private String code;
	private String message;
	
	private Code(String code,String message){
		this.code=code;
		this.message=message;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
