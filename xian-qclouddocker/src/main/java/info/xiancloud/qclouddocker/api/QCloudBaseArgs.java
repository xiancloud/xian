package info.xiancloud.qclouddocker.api;

/**
 * 公共请求参数
 * 
 * @author yyq
 *
 */
public class QCloudBaseArgs {

	// 具体接口名称
	private String Action;
	// 区域参数 
	private String Region;
	// 时间戳
	private long Timestamp;
	// 随机正整数
	private int Nonce;
	// 签名算法
	private String SignatureMethod;
	// 密匙ID
	private String SecretId;
	// 签名
	private String Signature;

	public String getAction() {
		return Action;
	}

	public void setAction(String action) {
		Action = action;
	}

	public String getRegion() {
		return Region;
	}

	public void setRegion(String region) {
		Region = region;
	}

	public long getTimestamp() {
		return Timestamp;
	}

	public void setTimestamp(long timestamp) {
		Timestamp = timestamp;
	}

	public int getNonce() {
		return Nonce;
	}

	public void setNonce(int nonce) {
		Nonce = nonce;
	}

	public String getSignatureMethod() {
		return SignatureMethod;
	}

	public void setSignatureMethod(String signatureMethod) {
		SignatureMethod = signatureMethod;
	}

	public String getSecretId() {
		return SecretId;
	}

	public void setSecretId(String secretId) {
		SecretId = secretId;
	}

	public String getSignature() {
		return Signature;
	}

	public void setSignature(String signature) {
		Signature = signature;
	}

}
