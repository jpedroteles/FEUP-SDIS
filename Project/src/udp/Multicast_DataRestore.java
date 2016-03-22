package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast_DataRestore implements Runnable {

	public static Thread mdr_thread;
	public static int mdr_port=8886;
	public static String mdr_address = "225.0.0.2";

	public Multicast_DataRestore() {
		mdr_thread = new Thread(this, "mdr_Thread created");
		mdr_thread.start();
	}

	public void mdr_communication(int port_number, String adress) throws IOException{

		MulticastSocket mdr = new MulticastSocket(port_number);
		InetAddress mdrAddress = InetAddress.getByName(adress);
		mdr.joinGroup(mdrAddress);

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536, mdrAddress, port_number);

		System.out.println("Awaiting to receive message");
		mdr.receive(packet);
		System.out.println("Message received");
		process_message(packet);
		mdr.leaveGroup(mdrAddress);
		mdr.close();
	}
	
	public void process_message(DatagramPacket packet) {
		System.out.println("Processing message");
	}

	public void run() {
		
		while(true) {
			System.out.println("mdr Thread is running");
			try {
				mdr_communication(mdr_port, mdr_address);
				Thread.sleep(1000);
			}
			catch(InterruptedException |IOException e) {
				break;
			}
		}		
	}
}


