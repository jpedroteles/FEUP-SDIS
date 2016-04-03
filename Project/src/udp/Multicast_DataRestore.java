package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_DataRestore implements Runnable {

	public static Thread mdr_thread;
	public static int mdr_port;
	public static String mdr_address;
	public static MulticastSocket mdr;
	public static InetAddress mdrAddress;
	public String ServerID;
	public ParseMessage pm = new ParseMessage();
	private static char crlf[] = {0xD,0xA};
	public History hist = new History();


	public Multicast_DataRestore(String ServerId, String mdr_a, int mdr_p) throws IOException{
		mdr_port=mdr_p;
		mdr_address=mdr_a;
		System.out.println(mdr_a+"--"+mdr_p);
		ServerID = ServerId;
		mdr = new MulticastSocket(mdr_port);
		mdrAddress = InetAddress.getByName(mdr_address);
		mdr.joinGroup(mdrAddress);
		mdr.setLoopbackMode(true);
		mdr_thread = new Thread(this, "mdr_Thread created");
		mdr_thread.start();
	}

	public void mdr_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		
		mdr.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		
		hist.add("-", pm.getId(header), pm.getChunkNum(header), pm.getSenderId(header), pm.getMessageType(header), "RECEIVED");

		System.out.println(header);
		
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID, mdr_port, mdr_address);
	}

	public void run() {
		
		while(true) {
			try {
				mdr_communication();
			}
			catch(IOException e) {
				break;
			}
		}
		
		try {
			mdr.leaveGroup(mdrAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mdr.close();
	}
}


