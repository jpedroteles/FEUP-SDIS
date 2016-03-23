package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast_DataBackup implements Runnable {

	public static Thread mdb_thread;
	public static int mdb_port=8887;
	public static String mdb_address = "225.0.0.3";

	public Multicast_DataBackup() {
		mdb_thread = new Thread(this, "mdb_Thread created");
		mdb_thread.start();
	}

	public void mdb_communication(int port_number, String adress) throws IOException{

		MulticastSocket mdb = new MulticastSocket(port_number);
		InetAddress mdbAddress = InetAddress.getByName(adress);
		mdb.joinGroup(mdbAddress);

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536, mdbAddress, port_number);

		System.out.println("Awaiting to receive message");
		mdb.receive(packet);
		System.out.println("Message received");
		process_message(packet);
		mdb.leaveGroup(mdbAddress);
		mdb.close();
	}
	
	public void process_message(DatagramPacket packet) {
		System.out.println("Processing message");
	}

	public void run() {
		
		while(true) {
			System.out.println("mdb Thread is running");
			try {
				mdb_communication(mdb_port, mdb_address);
				Thread.sleep(1000);
			}
			catch(InterruptedException |IOException e) {
				break;
			}
		}		
	}
}


