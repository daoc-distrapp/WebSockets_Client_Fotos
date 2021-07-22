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
					FotoMessage fotoMsg = objMap.readValue(message, FotoMessage.class);
					switch (fotoMsg.getType()) {
					case "GETLIST":
						System.out.println(fotoMsg.getListado());
						break;
					case "GETBYID":
						Foto foto = fotoMsg.getFoto();
						if(foto == null) {
							System.out.println("NO hay foto con esa ID !!!");
							break;
						}
						System.out.println(foto);
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
		FotoMessage fotoMsg = new FotoMessage(FotoMessage.GETBYID, "8", null);
		String json = obj2json.writeValueAsString(fotoMsg);
		System.out.println(json);
		
		wsClient.send(json);
		while(wsClient.hasBufferedData()) {
			Thread.sleep(1000);
		}
	}	
	
	private static void testGetList() throws Exception {
		FotoMessage fotoMsg = new FotoMessage(FotoMessage.GETLIST, "", null);
		String json = obj2json.writeValueAsString(fotoMsg);
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
		
		FotoMessage fotoMsg = new FotoMessage(FotoMessage.SAVE, "", foto);
		String json = obj2json.writeValueAsString(fotoMsg);
		System.out.println(json);
		
		wsClient.send(json);
		while(wsClient.hasBufferedData()) {
			Thread.sleep(1000);
		}

	}

}
