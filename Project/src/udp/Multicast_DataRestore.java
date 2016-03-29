package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_DataRestore implements Runnable {

	public static Thread mdr_thread;
	public static int mdr_port=8886;
	public static String mdr_address = "225.0.0.2";
	public static MulticastSocket mdr;
	public static InetAddress mdrAddress;
	public String ServerID;
	public ParseMessage pm = new ParseMessage();
	private static char crlf[] = {0xD,0xA};
	public History hist = new History();


	public Multicast_DataRestore(String ServerId) throws IOException{
		ServerID = ServerId;
		mdr = new MulticastSocket(mdr_port);
		mdrAddress = InetAddress.getByName(mdr_address);
		mdr.joinGroup(mdrAddress);
		mdr_thread = new Thread(this, "mdr_Thread created");
		mdr_thread.start();
	}

	public void mdr_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		
		mdr.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		
		System.out.println("RESTORED");
		hist.add("-", pm.getId(header), pm.getChunkNum(header), pm.getSenderId(header), pm.getMessageType(header), "RECEIVED");

		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID);
	}

	public void run() {
		
		while(true) {
			//System.out.println("mdr Thread is running");
			try {
				mdr_communication();
			}
			catch(IOException e) {
				//System.out.println("mdr - Exception");
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


