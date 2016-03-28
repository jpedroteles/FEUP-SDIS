package udp;

import java.io.IOException;
import java.net.DatagramPacket;

import parser.ParseMessage;
import protocols.Backup;
import protocols.Delete;
import protocols.Utils;

public class MessageProcessor implements Runnable {

	public Thread msg_thread;
	public String header;
	public byte[] content;
	public ParseMessage pm = new ParseMessage();

	public MessageProcessor(String h, byte[] c) {
		header = h;
		content = c;
		msg_thread = new Thread(this, "msg_Thread created");
		msg_thread.start();
	}

	public void process_message() throws IOException {
		
		String messageType = pm.getMessageType(header);

		switch(messageType){
		case("PUTCHUNK"): {
			Backup backup = new Backup(header, content);
			break;}
		case("DELETE"): {
			Delete delete = new Delete(header, content);
			break;}
		default: System.out.println("---> NOT Processing message"); break;
		}
	}

	public void run() {

		try {
			process_message();
			Thread.sleep(1000);
		}
		catch(InterruptedException | IOException e) {
			System.out.println("Process Message - Exception");
		}	
	}
}
