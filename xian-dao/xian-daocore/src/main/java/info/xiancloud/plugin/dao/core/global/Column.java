package info.xiancloud.plugin.dao.core.global;

public class Column {
	private String name;
	private Class<?> classType;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Class<?> getClassType() {
		return classType;
	}
	public void setClassType(Class<?> classType) {
		this.classType = classType;
	}
	
	public Column(String name, Class<?> classType) {
		this.name = name;
		this.classType = classType;
	}
	public static Column create(String name, Class<?> classType){
		return new Column(name, classType);
	}
}
