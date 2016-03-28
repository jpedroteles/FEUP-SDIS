package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;

public class Multicast_DataBackup implements Runnable {

	public static Thread mdb_thread;
	public static int mdb_port=8887;
	public static String mdb_address = "225.0.0.3";
	public static MulticastSocket mdb;
	public static InetAddress mdbAddress;
	public ParseMessage pm = new ParseMessage();
	private static char crlf[] = {0xD,0xA};

	public Multicast_DataBackup() throws IOException{
		mdb = new MulticastSocket(mdb_port);
		mdbAddress = InetAddress.getByName(mdb_address);
		mdb.joinGroup(mdbAddress);
		//mdb.setLoopbackMode(true);
		mdb_thread = new Thread(this, "mdb_Thread created");
		mdb_thread.start();
	}

	public void mdb_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		//System.out.println("MDB - waiting to receive message");
		mdb.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		
		MessageProcessor msg_pro = new MessageProcessor(header, content);
	}

	public void run() {
		
		while(true) {
			//System.out.println("mdb Thread is running");
			try {
				mdb_communication();
			}
			catch(IOException e) {
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


