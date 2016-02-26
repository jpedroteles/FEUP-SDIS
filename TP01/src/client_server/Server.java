package client_server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Vector;

public class Server {
	
	static Data_base db=new Data_base();
	public static void main(String[] args) throws IOException
	{
		Vector<String> v = new Vector<String>();
		v.add("Luis");
		v.add("00-00-00");
		db.data_base.addElement(v);
		int port_number=4445;
		communication(port_number);
	}

	public static void communication(int port_number) throws IOException
	{
		DatagramSocket socket = new DatagramSocket(port_number);
		
		while(true)
		{
			System.out.println("Waiting to receive packet");
			byte[] rbuf = new byte[1024];
			DatagramPacket packet = new DatagramPacket(rbuf, rbuf.length);
			socket.receive(packet);
			System.out.println("Packet received: ");
			
			String ret = parse(packet);
			if(ret == "EXIT")
			{
				break;
			}
			InetAddress address = InetAddress.getLocalHost();
			packet = new DatagramPacket(ret.getBytes(), ret.getBytes().length, address,packet.getPort());
			socket.send(packet);
			System.out.println("Packet sent");
		}
		socket.close();
	}

	public static String parse(DatagramPacket packet) {
		
		String received = new String(packet.getData());
		String[] temp=get_message(received).split(" ");
		String ret=new String();
		
		switch(temp[0]){
		case("REGISTER"):ret=Integer.toString(db.register(temp[2], temp[1])); break;
		case("LOOKUP"):ret=db.lookup(temp[1]);break;
		case("EXIT"):ret="EXIT";break;
		default:ret="ERROR";break;
		}
		
		return ret;
		
	}
	
	public static String get_message(String s)
	{
		String ret=new String();
		for(int i=0; i<s.length(); i++)
		{
			if(s.charAt(i)=='\0')
			{
				return ret;
			}
			ret+=(s.charAt(i));
		}
		return ret;
	}
}
