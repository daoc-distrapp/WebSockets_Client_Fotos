package dordonez.clients.foto_ws;

public class GenericMessage<T> {
	public static final String SAVE = "SAVE";
	public static final String GETLIST = "GETLIST";
	public static final String GETBYID = "GETBYID";
	public static final String DELETE = "DELETE";
	
	private String type;
	private String content;
	private T data;
	private String extra;
	
	public GenericMessage() {}
	
	public GenericMessage(String type, String content, T data, String extra) {
		this.type = type;
		this.content = content;
		this.data = data;
		this.extra = extra;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getExtra() {
		return extra;
	}
	public void setExtra(String extra) {
		this.extra = extra;
	}
	
	@Override
	public String toString() {
		return "GenericMessage [type=" + type + ", content=" + content + ", data=" + data + ", extra=" + extra +"]";
	}

}
