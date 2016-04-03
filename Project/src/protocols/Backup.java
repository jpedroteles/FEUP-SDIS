package protocols;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;

import parser.ParseMessage;
import udp.SendRequest;

public class Backup {
	/** Numero de porta multicast control */
	int mc_port;
	/** Adresso multicast control */
	String mc_address;
	/** Flag de fim */
	char crlf[] = {0xD,0xA};
	
	/** Construtor da classe backup esta classe trata de fazer o backup do chunk de um ficheiro
	* @param  header  header da mensagem
	* @param  content conteudo do chunk
	* @param  senderId identificador do cliente
	* @param  mc_p porta multicast control
	* @param  mc_a Adresso multicast
 	*/
	public Backup(String header, byte[] content, String ServerId, int mc_p, String mc_a) throws IOException, InterruptedException{
		mc_port=mc_p;
		mc_address = mc_a;
		ParseMessage pm = new ParseMessage();
		Utils utils = new Utils();
		SendRequest send = new SendRequest();
		History hist = new History();
		
		utils.checkFolder();

		String fileId = pm.getId(header) + "-" + pm.getChunkNum(header) + ".bin";
		if(!utils.checkFile(fileId)) {
			String name = "chunks/" + fileId;
			FileOutputStream out = new FileOutputStream(name);
			byte[] c = new byte[getSize(content)];
			System.arraycopy(content, 0, c, 0, c.length);
			out.write(c);
			
		}
		
		String reply = "STORED " + pm.getVersion(header) + " " + ServerId + " " + pm.getId(header) + " " + pm.getChunkNum(header) + " " + crlf[0]+crlf[1]+crlf[0]+crlf[1];	
		System.out.println(reply);
		send.sendRequest(reply.getBytes(), mc_port, mc_address, ServerId);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), ServerId, "STORED", "SENT");
	}

	/** Retorna o tamanho do conteudo
	* @param  content  conteudo
	* @return um inteiro com a dimensao do conteudo
 	*/		
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
