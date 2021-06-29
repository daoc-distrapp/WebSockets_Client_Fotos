package dordonez.clients.foto_ws;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Main {
	private static ObjectWriter obj2json = new ObjectMapper().writer().withDefaultPrettyPrinter();
	private static ObjectMapper objMap = new ObjectMapper();
	private static WebSocketClient wsClient;
	
	public static void main(String[] args) throws Exception {
		URI uri = new URI("ws://localhost:8080/fotos");
		wsClient = new WebSocketClient(uri) {			
			@Override
			public void onOpen(ServerHandshake handshakedata) {
				System.out.println("Conectado al server: " + uri);
			}			
			@Override
			public void onMessage(String message) {
				try {
					WsMessage wsMsg = objMap.readValue(message, WsMessage.class);
					switch (wsMsg.getType()) {
					case "GETLIST":
						System.out.println(wsMsg.getListado());
						break;
					case "GETBYID":
						Foto foto = wsMsg.getFoto();
						if(foto == null) {
							System.out.println("NO hay foto con esa ID !!!");
							break;
						}
						System.out.println(foto);
						System.out.println(foto.getFotoB64());
						byte[] decodedBytes = Base64.getDecoder().decode(foto.getFotoB64());
						Path path = Paths.get("C:\\Users\\ordon\\Downloads\\myfile.jpg");
						Files.write(path, decodedBytes);						
						break;			
					default:
						System.out.println("Caso indefinido: " + message);
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}		
			@Override
			public void onError(Exception ex) {
				ex.printStackTrace();
			}		
			@Override
			public void onClose(int code, String reason, boolean remote) {
				System.out.println("Desconectado del server: " + code);
			}
		};
		
		wsClient.connectBlocking();
		
		testSendFoto();
		testGetList();
		testGetById();

		wsClient.close();
	}

	private static void testGetById() throws Exception {
		WsMessage wsMsg = new WsMessage(WsMessage.GETBYID, "8", null);
		String json = obj2json.writeValueAsString(wsMsg);
		System.out.println(json);
		
		wsClient.send(json);
		while(wsClient.hasBufferedData()) {
			Thread.sleep(1000);
		}
	}	
	
	private static void testGetList() throws Exception {
		WsMessage wsMsg = new WsMessage(WsMessage.GETLIST, "", null);
		String json = obj2json.writeValueAsString(wsMsg);
		System.out.println(json);
		
		wsClient.send(json);
		while(wsClient.hasBufferedData()) {
			Thread.sleep(1000);
		}
	}
	
	private static void testSendFoto() throws Exception {
		Foto foto = new Foto();
		foto.setTitulo("dog05.jpg");
		foto.setDescripcion("Perro de prueba 05");
		byte[] bytes = Files.readAllBytes(Paths.get("dog05.jpg"));
		foto.setFotoB64(Base64.getEncoder().encodeToString(bytes));
		
		WsMessage wsMsg = new WsMessage(WsMessage.SAVE, "", foto);
		String json = obj2json.writeValueAsString(wsMsg);
		System.out.println(json);
		
		wsClient.send(json);
		while(wsClient.hasBufferedData()) {
			Thread.sleep(1000);
		}

	}

}
