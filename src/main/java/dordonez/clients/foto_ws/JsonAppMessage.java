package dordonez.clients.foto_ws;

public class JsonAppMessage {
	public static enum Types {SAVE, GETLIST, GETBYID, DELETE};
	public String type;
	public String message;
}
