package udp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseLog;

public class SendRequest {

	/** Envia um pedido do cliente para o servidor
	* @param message mensagem a ser enviada
	* @param port_number porta do canal a ser utilizado
	* @param address adereco do canal a ser utilizado
	* @param serverId identificador do servidor
 	*/
	public void sendRequest(byte[] message, int port_number, String address, String serverId) throws IOException, InterruptedException{

		MulticastSocket socket = new MulticastSocket(port_number);
		InetAddress socketAddress = InetAddress.getByName(address);
		socket.joinGroup(socketAddress);
		DatagramPacket packet = new DatagramPacket(message, message.length, socketAddress, port_number);
		socket.send(packet);
		String m =new String(message);
		String[] split = m.split(" ");
		System.out.println(m);
		if(split[0].equals("PUTCHUNK")){			
			System.out.println("PUTCHUNK: " + Integer.parseInt(split[5]));
			for(int i=1;i<5;i++){
				ParseLog pl = new ParseLog();
				if(pl.countRepDegree(split[3], split[4], serverId) < Integer.parseInt(split[5])){
					System.out.println("SLEEP " + i);
					Thread.sleep((int)(1000*Math.pow(2, i)));
					socket.send(packet);
					System.out.println("Package Sent");
				}
				else {
					break;
				}
			}
		}
		if(split[0].equals("GETCHUNK")){
			
			Thread.sleep(1000);
		}

		socket.leaveGroup(socketAddress);
		socket.close();
	}
}
