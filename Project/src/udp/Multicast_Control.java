package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast_Control implements Runnable {

	public static Thread mc_thread;
	public static int mc_port=8888;
	public static String mc_address = "224.0.0.19";
	public static MulticastSocket mc;
	public static InetAddress mcAddress;

	public Multicast_Control() throws IOException{
		mc = new MulticastSocket(mc_port);
		mcAddress = InetAddress.getByName(mc_address);
		mc.joinGroup(mcAddress);
		mc.setLoopbackMode(true);
		mc_thread = new Thread(this, "mc_Thread created");
		mc_thread.start();
	}

	public void mc_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		System.out.println("MC - waiting to receive message");
		mc.receive(packet);
		
		MessageProcessor msg_pro = new MessageProcessor(packet);
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


