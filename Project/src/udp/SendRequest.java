package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class SendRequest {
	
	public void sendRequest(String message, int port_number, String address) throws IOException{
		
		MulticastSocket socket = new MulticastSocket(port_number);
		InetAddress socketAddress = InetAddress.getByName(address);
		socket.joinGroup(socketAddress);
		
		DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, socketAddress, port_number);
		System.out.println("Sending Package");
		socket.send(packet);
		System.out.println("Package Sent");	
		
		socket.leaveGroup(socketAddress);
		socket.close();
	}
}
