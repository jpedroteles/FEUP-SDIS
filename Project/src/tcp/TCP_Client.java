package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class TCP_Client {

	/** Construtor da classe TCP_Client, esta classe da ligacao tcp do cliente
	* @param  args  array com os argumentos que constituem a operacao a ser realizada
 	*/
	public void communication(String[] args) throws IOException {
		
		InetAddress IP=InetAddress.getLocalHost();
		String hostname = IP.getHostName();
		int portNumber=Integer.parseInt(args[0]);
		String temp = String.join(" ", args);
		String message=get_message(temp);
		 
		String response;   
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
		if(inFromUser.equals(null)) {
			System.out.println("Error: TCP-Client");
		}
		Socket clientSocket = new Socket(hostname, portNumber);   
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
		   
		outToServer.writeBytes(message + '\n');   
		response = inFromServer.readLine();
					
		System.out.println("Response: " + response);   
		clientSocket.close(); 
	}
	
	/** Retorna uma mensagem
	* @param  arg  string com todos os argumentos
	* @return ret string com a mensagem
 	*/	
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
