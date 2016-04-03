package protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import parser.ParseMessage;
import udp.SendRequest;

public class Reclaim {
	/** Numero de porta multicast control */
	int mc_port;
	/** Adereco multicast control */
	String mc_address;
	/** Flag de fim */
	char crlf[] = {0xD,0xA};

	/** Construtor da classe Reclaim esta classe trata de fazer o reclaim do chunk de um ficheiro
	* @param  header  header da mensagem
	* @param  content conteudo do chunk
	* @param  senderId identificador do cliente
	* @param  pt porta multicast control
	* @param  a Adereco multicast
 	*/
	public Reclaim(String header, byte[] content, String serverId, int pt, String a) throws IOException{
		mc_port=pt;
		mc_address=a;
		ParseMessage pm = new ParseMessage();
		Utils utils = new Utils();
		SendRequest send = new SendRequest();
		History hist = new History();

		utils.checkFolder();
		File folder = new File("chunks");
		File[] files = folder.listFiles();

		String path= "chunks/" + pm.getId(header)+"-"+pm.getChunkNum(header)+".bin"; 
		Path p = FileSystems.getDefault().getPath(path);
		Files.delete(p);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), serverId, "REMOVED", "SENT");

	}


}
