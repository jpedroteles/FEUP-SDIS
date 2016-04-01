package communication;

import java.io.IOException;

import parser.ParseLog;
import tcp.TCP_Server;
import udp.*;

public class Server {

	static String ServerId="Server";
	
	public static void main(String args[]) throws IOException {
		
		TCP_Server tcp_server = new TCP_Server(ServerId);
		Multicast_Control mc = new Multicast_Control(ServerId);
		Multicast_DataRestore mdr = new Multicast_DataRestore(ServerId);
		Multicast_DataBackup mdb = new Multicast_DataBackup(ServerId);
	}
}
