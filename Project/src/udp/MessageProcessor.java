package udp;

import java.io.IOException;
import java.net.DatagramPacket;

public class MessageProcessor implements Runnable {

	public static Thread msg_thread;
	public static DatagramPacket pack;

	public MessageProcessor(DatagramPacket packet ) {
		pack=packet;
		msg_thread = new Thread(this, "msg_Thread created");
		msg_thread.start();

	}

	public void process_message(DatagramPacket packet) {
		System.out.println("---> Processing message: ");

	}

	public void run() {
		
		try {
			process_message(pack);
			Thread.sleep(10);
		}
		catch(InterruptedException e) {
			System.out.println("Process Message - Exception");
		}	
	}
}
