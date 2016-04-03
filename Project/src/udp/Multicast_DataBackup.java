package udp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_DataBackup implements Runnable {

	public static Thread mdb_thread;
	public static int mdb_port;
	public static String mdb_address;
	public static int mc_port;
	public static String mc_address;
	public static MulticastSocket mdb;
	public static InetAddress mdbAddress;
	public ParseMessage pm = new ParseMessage();
	private static char crlf[] = {0xD,0xA};
	public String ServerID;
	public History hist = new History();

	public Multicast_DataBackup(String ServerId, String mdb_a, int mdb_p, String mc_a, int mc_p) throws IOException{
		mdb_port=mdb_p;
		mdb_address=mdb_a;
		mc_port=mc_p;
		mc_address=mc_a;
		ServerID = ServerId;
		mdb = new MulticastSocket(mdb_port);
		mdbAddress = InetAddress.getByName(mdb_address);
		mdb.joinGroup(mdbAddress);
		mdb.setLoopbackMode(true);
		mdb_thread = new Thread(this, "mdb_Thread created");
		mdb_thread.start();
	}

	public void mdb_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		mdb.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), pm.getSenderId(header), pm.getMessageType(header), "RECEIVED");
		

		System.out.println(header);
		
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID, mc_port, mc_address);
		
	}

	public void run() {
		
		while(true) {
			try {
				mdb_communication();
			}
			catch(IOException e) {
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


