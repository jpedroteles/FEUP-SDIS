package multicast;

import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {

	public static String mcast_addr;
	public static int mcast_port;
	public static String srvc_addr;
	public static int srvc_port;
	public static String message;
	public static MulticastSocket multiSocket;
	
	//224.0.0.3 8000 REGISTER 00-00-LD Luis
	public static void main(String[] args) throws IOException {
		
		String input = get_args();
		System.out.println("INPUT: " + input);
		
		String[] input_args = input.split(" ");
		
		//Check if the number of arguments received is correct
		if(input_args.length < 4) {
			System.out.println("Invalid number of arguments!");
			return;
		}
		//Multicast address and port
		mcast_addr = input_args[0];
		mcast_port = Integer.parseInt(input_args[1]);
		
		System.out.println("mcast_addr: " + mcast_addr + " mcast_port: " + mcast_port);
		//lookup service address
		String service=lookupService();
		System.out.println("MULTICAST PACKET: " + service);
		
		//Response treatment
		String[] responseArgs = service.trim().split(":");
		srvc_addr = responseArgs[0];
		srvc_port = Integer.parseInt(responseArgs[1].replaceAll("[^\\d.]", ""));

		System.out.println("MULTICAST: " + mcast_addr + ":" + mcast_port + " - " + srvc_addr + ":" + srvc_port);

		message = get_message(input);
		System.out.println("MESSAGE: " + message);
		
		sendRequest();
	}

	private static void sendRequest() throws IOException {
		
		System.out.println("Initiating send request");
		DatagramSocket clientSocket = new DatagramSocket();

		// Send the request
		System.out.println("ADDR: -" + srvc_addr + "-");
		InetAddress address = InetAddress.getByName(srvc_addr);
		byte[] requestData = message.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(requestData, requestData.length, address, srvc_port);
		clientSocket.send(sendPacket);

		System.out.println("Message sent");
		
		// Receive the request
		byte[] receivedData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
		clientSocket.receive(receivePacket);
		
		System.out.println("FINAL:" + new String(receivePacket.getData(),0,receivePacket.getLength()));
		
		// Close client socket
		clientSocket.close();
		multiSocket.leaveGroup(InetAddress.getByName(mcast_addr));
		multiSocket.close();
		
	}

	private static String lookupService() throws IOException {
		System.out.println("Initiating lookupService");
		InetAddress group = InetAddress.getByName(mcast_addr);
		multiSocket= new MulticastSocket(mcast_port);
		multiSocket.joinGroup(group);
		System.out.println("Initiating lookupService - Read from server");
		//Read from server
		byte[] buffer = new byte[256];
		DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
		multiSocket.receive(packet);
		System.out.println("PACKET DATA" + packet.getData());
		return new String(packet.getData());
	}
	
	private static String get_args()
	{
		 Scanner scanner = new Scanner(System.in);
         String ret;
         ret=scanner.nextLine();
         return ret;
	}
	
	public static String get_message(String arg)
	{
		String ret = new String();
		String[] temp = arg.split(" ");

		if(temp[2].equals("REGISTER"))
		{
			ret+="REGISTER";
			for(int i=3; i<temp.length;i++)
			{
				ret+=" " + temp[i];
			}
		}
		else if(temp[2].equals("LOOKUP"))
		{
			ret+="LOOKUP " + temp[3];
		}
		else
		{
			ret+="EXIT";
		}
		
		return ret;
	}
}
