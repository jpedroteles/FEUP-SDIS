package protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import parser.ParseMessage;
import udp.SendRequest;

public class Delete {

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
				System.out.println("Chunk deleted");

				hist.add("-", pm.getId(header), Integer.toString(i), serverId, "-", "SENT");
			}
		}
	}
	
	public String getFileId(File files){
		String[] split=files.getName().split("-");
		return split[0];
	}
}
