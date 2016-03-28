package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;

public class Multicast_Control implements Runnable {

	public static Thread mc_thread;
	public static int mc_port=8885;
	public static String mc_address = "225.0.0.1";
	public static MulticastSocket mc;
	public static InetAddress mcAddress;
	public ParseMessage pm = new ParseMessage();
	private static char crlf[] = {0xD,0xA};
	public String ServerID;

	public Multicast_Control(String ServerId) throws IOException{
		ServerID = ServerId;
		mc = new MulticastSocket(mc_port);
		mcAddress = InetAddress.getByName(mc_address);
		mc.joinGroup(mcAddress);
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
		
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID);
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


