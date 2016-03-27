package protocols;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import parser.ParseMessage;

public class Delete {
	private static char crlf[] = {0xD,0xA};

	public Delete(String header, byte[]content) {

	}

	public void deleteFile(DatagramPacket packet, ParseMessage message) throws IOException{
		String path= "/chunks/" + message.getFileId(message.getHeader(packet, crlf));		
		Path p = FileSystems.getDefault().getPath(path);
		Files.delete(p);
			
	}
	
}
