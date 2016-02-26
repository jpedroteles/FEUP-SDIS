package client_server;

import java.io.*;
import java.net.*;

public class Client {

	public static void main(String[] args) throws IOException
	{
		String[] test = new String[]{"localhost 4445 LOOKUP 00-00-00"};
		if(test.length == 1)
		{
			communication(test);
		}
		else
		{
			System.out.println("Usage: java Echo <hostname> <port_number> <oper> <opnd>");
			return;
		}
	}

	public static void communication(String[] args) throws IOException
	{
		// send request
		DatagramSocket socket = new DatagramSocket();
		byte[] sbuf = get_message(args[0]).getBytes();
		InetAddress address = InetAddress.getByName("localhost");
		DatagramPacket packet = new DatagramPacket(sbuf, sbuf.length, address, get_portNumber(args[0]));
		socket.send(packet);
		System.out.println("Packet sent: " + new String(packet.getData()));
		
		// get response
		byte[] rbuf = new byte[sbuf.length];
		packet = new DatagramPacket(rbuf, rbuf.length);
		System.out.println("Waiting to receive response packet");
		socket.receive(packet);
		
		// display response
		String received = new String(packet.getData());
		print_message(received);
		socket.close();
	}

	public static int get_portNumber(String arg)
	{
		String temp[]=arg.split(" ");
		int ret=Integer.parseInt(temp[1]);
		return ret;
	}
	
	public static String get_message(String arg)
	{
		String ret = new String();
		String[] temp = arg.split(" ");

		System.out.println("temp2: " + temp[2]);
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

	public static void print_message(String s)
	{
		for(int i=0; i<s.length(); i++)
		{
			if(s.charAt(i)=='\0')
			{
				return;
			}
			System.out.print(s.charAt(i));
		}
	}
	
}
