package protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import parser.ParseMessage;
import udp.SendRequest;

public class Reclaim {
	
	int mc_port=8885;
	String mc_address = "225.0.0.1";
	char crlf[] = {0xD,0xA};
	
	public Reclaim(String header, byte[] content, String serverId) throws IOException{
		
		ParseMessage pm = new ParseMessage();
		int size = Integer.parseInt(pm.getId(header));
		System.out.println("SIZE: " + size);
		Utils utils = new Utils();
		SendRequest send = new SendRequest();
		
		utils.checkFolder();
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		
		for(int i=0; i<files.length;i++){
			if(size > 0){
				String path= "chunks/" + files[i].getName();      
		        Path p = FileSystems.getDefault().getPath(path);
		        size-=files[i].length();
		        Files.delete(p);
		        String reply = "REMOVED " + pm.getVersion(header) + " " + serverId + " " + getFileId(files[i]) + " " + getChunkNum(files[i]) + " " + crlf[0]+crlf[1]+crlf[0]+crlf[1];
		        send.sendRequest(reply.getBytes(), mc_port, mc_address);
			}
		}
		
		if(size > 0){
			System.out.println("RECLAIM: " + size);
		}
		else{
			System.out.println("RECLAIM: " + 0);
		}
		
	}
	
	public String getFileId(File files){
		String[] split=files.getName().split("-");
		return split[0];
	}
	
	public String getChunkNum(File files){
		String[] split=files.getName().split("-");
		return split[1];
	}

}
