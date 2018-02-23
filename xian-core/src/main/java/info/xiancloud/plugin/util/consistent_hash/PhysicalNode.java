package info.xiancloud.plugin.util.consistent_hash;
/**
 * 
 * @author songwenjun
 *
 */
public class PhysicalNode {
	private String domain;
	private String ip;
	private int port;	
	 
	public PhysicalNode(String domain,String ip,int port){
		this.domain=domain;
		this.ip=ip;
		this.port=port;
	}
	
	@Override
	public String toString() {
		return domain+":"+ ip+":"+ port ;
	}

	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
}
