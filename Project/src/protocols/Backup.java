package protocols;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import parser.ParseMessage;
import udp.SendRequest;

public class Backup {
	
	int mc_port=8885;
	String mc_address = "225.0.0.1";
	char crlf[] = {0xD,0xA};
	
	public Backup(String header, byte[] content, String ServerId) throws IOException{
		
		ParseMessage pm = new ParseMessage();
		Utils utils = new Utils();
		SendRequest send = new SendRequest();
		History hist = new History();
		
		utils.checkFolder();
		String fileId = pm.getId(header) + "-" + pm.getChunkNum(header) + ".bin";
		if(!utils.checkFile(fileId)) {
			String name = "chunks/" + fileId;
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "utf-8"));
			writer.write(new String(content));
			//O sender Id deve ser o do server que recebeu nao o que enviou
			
			System.out.println("Reply: " + 0);
		}
		else {
			System.out.println("Reply: " + -1);
		}
		
		String reply = "STORED " + pm.getVersion(header) + " " + ServerId + " " + pm.getId(header) + " " + pm.getChunkNum(header) + " " + crlf[0]+crlf[1]+crlf[0]+crlf[1];	
		send.sendRequest(reply.getBytes(), mc_port, mc_address);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), ServerId, "STORED", "SENT");
	}
}
