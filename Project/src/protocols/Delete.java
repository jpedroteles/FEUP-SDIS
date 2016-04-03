package protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import parser.ParseMessage;
import udp.SendRequest;

public class Delete {

 /** Construtor da classe Dele esta classe trata de fazer o apagar o chunk de um ficheiro
	* @param  header  header da mensagem
	* @param  content conteudo do chunk
	* @param  senderId identificador do cliente
 	*/
	public Delete(String header, byte[]content, String serverId) throws IOException{
		
		ParseMessage pm = new ParseMessage();
		Utils utils = new Utils();
		History hist = new History();
		
		utils.checkFolder();
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		
		for(int i=0; i<files.length;i++){
			if(pm.getId(header).equals(getFileId(files[i]))){
				String path= "chunks/" + files[i].getName();      
		        Path p = FileSystems.getDefault().getPath(path);
		        Files.delete(p);

				hist.add("-", pm.getId(header), Integer.toString(i), serverId, "-", "SENT");
			}
		}

		System.out.println("FILE DELETED");
	}
	
	/** Retorna o identificador do ficheiro
	* @param  files  com os dados do ficheiro
	* @return split inteiro com o identificador
 	*/
	public String getFileId(File files){
		String[] split=files.getName().split("-");
		return split[0];
	}
}
