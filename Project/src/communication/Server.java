package communication;

import tcp.TCP_Server;
import udp.*;

public class Server {

	public static void main(String args[]) {
		TCP_Server tcp_server = new TCP_Server();
		Multicast_Control mc = new Multicast_Control();
		Multicast_DataRestore mdr = new Multicast_DataRestore();
		Multicast_DataBackup mdb = new Multicast_DataBackup();
	}
}
