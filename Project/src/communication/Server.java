package communication;

import java.io.IOException;

import parser.ParseLog;
import tcp.TCP_Server;
import udp.*;

public class Server {
	
	public static void main(String args[]) throws IOException {
		
		TCP_Server tcp_server = new TCP_Server(args[0], args[1], Integer.parseInt(args[2]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[7]));
		Multicast_Control mc = new Multicast_Control(args[0], args[1], Integer.parseInt(args[2]), args[5], Integer.parseInt(args[6]));
		Multicast_DataBackup mdb = new Multicast_DataBackup(args[0], args[3], Integer.parseInt(args[4]), args[1], Integer.parseInt(args[2]));
		Multicast_DataRestore mdr = new Multicast_DataRestore(args[0], args[5], Integer.parseInt(args[6]));
	}
}
