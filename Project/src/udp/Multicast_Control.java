package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Multicast_Control implements Runnable {

	public static Thread mc_thread;
	public static int mc_port=8885;
	public static String mc_address = "225.0.0.1";

	public Multicast_Control() {
		mc_thread = new Thread(this, "MC_Thread created");
		mc_thread.start();
	}

	public void mc_communication(int port_number, String adress) throws IOException{

		MulticastSocket mc = new MulticastSocket(port_number);
		InetAddress mcAddress = InetAddress.getByName(adress);
		mc.joinGroup(mcAddress);

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536, mcAddress, port_number);

		System.out.println("Awaiting to receive message");
		mc.receive(packet);
		System.out.println("Message received");
		process_message(packet);
		mc.leaveGroup(mcAddress);
		mc.close();
	}
	
	public void process_message(DatagramPacket packet) {
		System.out.println("Processing message");
	}

	public void run() {
		
		while(true) {
			System.out.println("MC Thread is running");
			try {
				mc_communication(mc_port, mc_address);
				Thread.sleep(1000);
			}
			catch(InterruptedException |IOException e) {
				break;
			}
		}		
	}
}

