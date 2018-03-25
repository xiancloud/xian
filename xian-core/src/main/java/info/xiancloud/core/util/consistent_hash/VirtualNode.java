package info.xiancloud.core.util.consistent_hash;
/**
 * 
 * @author songwenjun
 *
 */
public class VirtualNode {   
    private int replicaNumber;  
    private PhysicalNode parent;

	public VirtualNode(PhysicalNode parent, int replicaNumber) {    	
        this.replicaNumber = replicaNumber; 
        this.parent = parent;
    }

    public boolean matches(String host) {
        return parent.toString().equalsIgnoreCase(host);
    }	

	@Override
    public String toString() {
        return parent.toString().toLowerCase() + ":" + replicaNumber;
    }

	public int getReplicaNumber() {
		return replicaNumber;
	}

	public void setReplicaNumber(int replicaNumber) {
		this.replicaNumber = replicaNumber;
	}	

    public PhysicalNode getParent() {
		return parent;
	}
}
