package info.xiancloud.qcloudcos.api.server;

/**
 * 响应实体
 * 
 * @author yyq
 *
 */
public class ResponseEntity {

	private String code;
	private String errMsg;
	private Object data;

	public static ResponseEntity build() {
		ResponseEntity response = new ResponseEntity();
		return response;
	}

	public static ResponseEntity build(Code code) {
		ResponseEntity response = new ResponseEntity();
		response.setCode(code.name());
		response.setErrMsg(code.getMessage());
		return response;
	}

	public static ResponseEntity buildSuc() {
		ResponseEntity response = build(Code.SUCCESS);
		return response;
	}

	public static ResponseEntity buildSuc(String data) {
		ResponseEntity response = buildSuc();
		response.setData(data);
		return response;
	}

	public static ResponseEntity buildFail(String message) {
		ResponseEntity response = build(Code.Fail);
		response.setErrMsg(message);
		return response;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

}
