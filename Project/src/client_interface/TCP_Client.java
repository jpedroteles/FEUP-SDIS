package client_interface;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_Client {

	public void communication(String[] args) throws IOException {
		
		InetAddress IP=InetAddress.getLocalHost();
		String hostname = IP.getHostName();
		int portNumber=Integer.parseInt(args[0]);
		String temp = String.join(" ", args);
		String message=get_message(temp);
		 
		String response;   
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
		Socket clientSocket = new Socket(hostname, portNumber);   
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
		   
		outToServer.writeBytes(message + '\n');   
		response = inFromServer.readLine();
					
		System.out.println("Response: " + response);   
		clientSocket.close(); 
	}
	
	public static String get_message(String arg) {
		
		String ret = new String();
		String[] temp = arg.split(" ");

		ret=temp[1];
		ret+=" " + temp[2];
		if(temp.length == 4) {
			ret+=" " + temp[3];
		}
		else {
			ret+=" 0";
		}
		return ret;
	}

}
