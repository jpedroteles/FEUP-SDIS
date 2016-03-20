package communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	
	public static void main(String[] args) throws IOException {
		
		if(args.length != 1) {
			System.out.println("Usage: java Echo <port_number>");
			return;
		}
		
		int port_number=Integer.parseInt(args[0]);
		TCP_Server tcp = new TCP_Server();
		
		tcp.communication(port_number);
	}
}
