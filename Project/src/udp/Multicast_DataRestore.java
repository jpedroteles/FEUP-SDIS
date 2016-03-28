package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;

public class Multicast_DataRestore implements Runnable {

	public static Thread mdr_thread;
	public static int mdr_port=8886;
	public static String mdr_address = "225.0.0.2";
	public static MulticastSocket mdr;
	public static InetAddress mdrAddress;
	public String ServerID;


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

		//System.out.println("MDR - waiting to receive message");
		mdr.receive(packet);
		//MessageProcessor msg_pro = new MessageProcessor(packet);
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


