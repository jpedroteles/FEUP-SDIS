package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_Control implements Runnable {

	public static Thread mc_thread;
	/*public static int mc_port=8885;
	public static String mc_address = "225.0.0.1";*/
	public static int mc_port;
	public static String mc_address;
	public static int mdr_port;
	public static String mdr_address;
	public static MulticastSocket mc;
	public static InetAddress mcAddress;
	public ParseMessage pm = new ParseMessage();
	private static char crlf[] = {0xD,0xA};
	public String ServerID;
	History hist = new History();

	public Multicast_Control(String ServerId, String mc_a, int mc_p, String mdr_a, int mdr_p) throws IOException{
		mc_port=mc_p;
		mc_address=mc_a;
		mdr_port=mdr_p;
		mdr_address=mdr_a;
		ServerID = ServerId;
		mc = new MulticastSocket(mc_port);
		mcAddress = InetAddress.getByName(mc_address);
		mc.joinGroup(mcAddress);
		//mc.setLoopbackMode(true);
		mc_thread = new Thread(this, "mc_Thread created");
		mc_thread.start();
	}

	public void mc_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		//System.out.println("MC - waiting to receive message");
		mc.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), ServerID, pm.getMessageType(header), "RECEIVED");
		//System.out.println("MC");
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID, mdr_port, mdr_address);
	}

	public void run() {
		
		while(true) {
			//System.out.println("mc Thread is running");
			try {
				mc_communication();
			}
			catch(IOException e) {
				//System.out.println("mc - Exception");
				break;
			}
		}
		
		try {
			mc.leaveGroup(mcAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mc.close();
	}
}


