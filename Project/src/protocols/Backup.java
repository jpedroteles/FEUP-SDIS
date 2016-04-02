package protocols;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import parser.ParseMessage;
import udp.SendRequest;

public class Backup {
	
	int mc_port;
	String mc_address;
	char crlf[] = {0xD,0xA};
	
	public Backup(String header, byte[] content, String ServerId, int mc_p, String mc_a) throws IOException, InterruptedException{
		mc_port=mc_p;
		mc_address = mc_a;
		ParseMessage pm = new ParseMessage();
		Utils utils = new Utils();
		SendRequest send = new SendRequest();
		History hist = new History();
		
		utils.checkFolder();

		//System.out.println("SIZE2: "+ new String(content).getBytes().length);
		//System.out.println(new String(content));
		String fileId = pm.getId(header) + "-" + pm.getChunkNum(header) + ".bin";
		if(!utils.checkFile(fileId)) {
			String name = "chunks/" + fileId;
			//System.out.println("CONTENT SIZE: "+ content.length);
			FileOutputStream out = new FileOutputStream(name);
			byte[] c = new byte[getSize(content)];
			System.arraycopy(content, 0, c, 0, c.length);
			out.write(c);
			
			System.out.println("Reply: " + 0);
		}
		else {
			System.out.println("Reply: " + -1);
		}
		
		String reply = "STORED " + pm.getVersion(header) + " " + ServerId + " " + pm.getId(header) + " " + pm.getChunkNum(header) + " " + crlf[0]+crlf[1]+crlf[0]+crlf[1];	
		send.sendRequest(reply.getBytes(), mc_port, mc_address, ServerId);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), ServerId, "STORED", "SENT");
	}
	
	public int getSize(byte[] content){
		int ret=0;
		for(int i=0;i<content.length;i++){
			if(content[i]!=0){
				ret++;
			}
		}
		return ret;
	}
}
