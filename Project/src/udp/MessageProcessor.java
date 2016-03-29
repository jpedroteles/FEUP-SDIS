package udp;

import java.io.IOException;

import parser.ParseMessage;
import protocols.Backup;
import protocols.Delete;
import protocols.Reclaim;

public class MessageProcessor implements Runnable {

	public Thread msg_thread;
	public String header;
	public byte[] content;
	public ParseMessage pm = new ParseMessage();
	public String serverId;

	public MessageProcessor(String h, byte[] c, String ServerID) {
		serverId=ServerID;
		header = h;
		content = c;
		msg_thread = new Thread(this, "msg_Thread created");
		msg_thread.start();
	}

	public void process_message() throws IOException {
		
		String messageType = pm.getMessageType(header);

		switch(messageType){
		case("PUTCHUNK"): {
			Backup backup = new Backup(header, content, serverId);
			break;}
		case("DELETE"): {
			Delete delete = new Delete(header, content, serverId);
			break;}
		case("REMOVED"): {
			Reclaim reclaim = new Reclaim(header, content, serverId);
			break;}
		default: break;
		}
	}

	public void run() {

		try {
			process_message();
			Thread.sleep(1000);
		}
		catch(InterruptedException | IOException e) {
		}	
	}
}
