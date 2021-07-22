package dordonez.clients.foto_ws;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class Test {

	/**
	 * Prueba de uso del GenericMessage:
	 * al tener tipo genérico, requiere del TypeReference para el ObjectMapper.readValue() !!!
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ObjectWriter obj2json = new ObjectMapper().writer().withDefaultPrettyPrinter();
		ObjectMapper json2obj = new ObjectMapper();
		TypeReference<GenericMessage<Foto>> objTypeReference = new TypeReference<GenericMessage<Foto>>() {};
		
		Foto foto = new Foto();
		foto.setTitulo("dog01.jpg");
		foto.setDescripcion("Perro de prueba 01");
		byte[] bytes = Files.readAllBytes(Paths.get("dog01.jpg"));
		foto.setFotoB64(Base64.getEncoder().encodeToString(bytes));
		
		GenericMessage<Foto> sendMsg = new GenericMessage<>(GenericMessage.SAVE, "", foto, "");
		String json = obj2json.writeValueAsString(sendMsg);
		//System.out.println(json);
		
		GenericMessage<Foto> rcvMsg = json2obj.readValue(json, objTypeReference);
		System.out.println(rcvMsg);
		
		byte[] decodedBytes = Base64.getDecoder().decode(foto.getFotoB64());
		Path path = Paths.get("C:\\Users\\ordon\\Downloads\\myfile.jpg");
		Files.write(path, decodedBytes);
	}

}
