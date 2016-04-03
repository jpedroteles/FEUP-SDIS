package communication;

import java.io.IOException;

import parser.ParseLog;
import tcp.TCP_Server;
import udp.*;

public class Server {
	
	/** Inicializa a comunicacao tcp do servidor
	* @param args argumentos utilizar
 	*/
	public static void main(String args[]) throws IOException {
		
		if(args.length!=8){
			System.out.println("Usage: java Echo <senderId> <mc_address> <mc_port> <mdb_address> <mdb_port> <mdr_address> <mdr_port> <tcp_port> ");
			return;
		}
		
		TCP_Server tcp_server = new TCP_Server(args[0], args[1], Integer.parseInt(args[2]), args[3], Integer.parseInt(args[4]), Integer.parseInt(args[7]));
		Multicast_Control mc = new Multicast_Control(args[0], args[1], Integer.parseInt(args[2]), args[5], Integer.parseInt(args[6]));
		Multicast_DataBackup mdb = new Multicast_DataBackup(args[0], args[3], Integer.parseInt(args[4]), args[1], Integer.parseInt(args[2]));
		Multicast_DataRestore mdr = new Multicast_DataRestore(args[0], args[5], Integer.parseInt(args[6]));
	}
}
