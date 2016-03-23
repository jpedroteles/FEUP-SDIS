package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast_DataBackup implements Runnable {

	public static Thread mdb_thread;
	public static int mdb_port=8887;
	public static String mdb_address = "225.0.0.3";
	public static MulticastSocket mdb;
	public static InetAddress mdbAddress;

	public Multicast_DataBackup() throws IOException{
		mdb = new MulticastSocket(mdb_port);
		mdbAddress = InetAddress.getByName(mdb_address);
		mdb.joinGroup(mdbAddress);
		mdb_thread = new Thread(this, "mdb_Thread created");
		mdb_thread.start();
	}

	public void mdb_communication(int port_number, String adress) throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536, mdbAddress, port_number);

		System.out.println("Awaiting to receive message");
		mdb.receive(packet);
		
		MessageProcessor msg_pro = new MessageProcessor(packet);
	}

	public void run() {
		
		while(true) {
			//System.out.println("mdb Thread is running");
			try {
				mdb_communication(mdb_port, mdb_address);
				Thread.sleep(1000);
			}
			catch(InterruptedException |IOException e) {
				//System.out.println("MDB - Exception");
				break;
			}
		}
		
		try {
			mdb.leaveGroup(mdbAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mdb.close();
	}
}


