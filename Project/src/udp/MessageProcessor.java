package udp;

import java.io.IOException;

import parser.Assemble;
import parser.ParseMessage;
import protocols.Backup;
import protocols.Delete;
import protocols.Reclaim;
import protocols.Restore;

public class MessageProcessor implements Runnable {
	/** Thread de tratamento da mensagem */
	public Thread msg_thread;
	/** Headder da mensagem */
	public String header;
	/** Conteudo da mensagem */
	public byte[] content;
	/** Parse da mensage */
	public ParseMessage pm = new ParseMessage();
	/** Identificador do servidor */
	public String serverId;
	/** Numero de porta multicast */
	int port;
	/** Adereco de porta multicast */
	String address;

	
	/** Construtor do processador da mensagem dos canais udp
	* @param h header da mensagem
	* @param c conteudo da mensage
	* @param ServerId Identificador do servidor
	* @param p porta do canal de comunicacao
	* @param a adereco do do canal de comunicacao
 	*/
	public MessageProcessor(String h, byte[] c, String ServerID, int p, String a) {
		port=p;
		address=a;
		serverId=ServerID;
		header = h;
		content = c;
		msg_thread = new Thread(this, "msg_Thread created");
		msg_thread.start();
	}

	/** Processa a mensagem recebida e inicializa os protocolos respetivos
 	*/
	public void process_message() throws IOException, InterruptedException {
		
		String messageType = pm.getMessageType(header);

		switch(messageType){
		case("PUTCHUNK"): {
			Backup backup = new Backup(header, content, serverId, port, address);
			break;}
		case("DELETE"): {
			Delete delete = new Delete(header, content, serverId);
			break;}
		case("REMOVED"): {
			Reclaim reclaim = new Reclaim(header, content, serverId, port, address);
			break;}
		case("GETCHUNK"): {
			Restore restore = new Restore(header, serverId, port, address);
			break;}
		case("CHUNK"): {
			Assemble assemble = new Assemble(header, content, serverId);
			break;}
		default: break;
		}
	}
	
	/** Thread que gere o processamento da mensagem
 	*/
	public void run() {

		try {
			process_message();
			Thread.sleep(1000);
		}
		catch(InterruptedException | IOException e) {
		}	
	}
}
