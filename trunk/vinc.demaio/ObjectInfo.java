
public class ObjectInfo {

	public ObjectInfo(Object data) {
		this.data = data;
		this.typename = "";
	}
	
	public ObjectInfo(String typename, Object data) {
		this.typename = typename;
		this.data = data;
	}
	
	public String getTypename() {
		return typename;
	}
	
	public void setTypename(String typename) {
		this.typename = typename;
	}

	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	private String typename;
	private Object data;
}
