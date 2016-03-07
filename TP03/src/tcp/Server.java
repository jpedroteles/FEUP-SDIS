package tcp;

import java.io.*;
import java.net.*;
import java.util.Vector;


public class Server {
	
	public static Data_base db;
	public static int port_number;
	public static void main(String[] args) throws IOException
	{
		if(args.length != 1)
		{
			System.out.println("Usage: java Echo <port_number>");
			return;
		}
		
		db=new Data_base();
		port_number=Integer.parseInt(args[0]);
			
		communication();
	}

	public static void communication() throws IOException
	{       
		String input;
		String response;
		ServerSocket welcomeSocket = new ServerSocket(port_number);          
		while(true){             
			Socket connectionSocket = welcomeSocket.accept();             
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
			input = inFromClient.readLine();             
			System.out.println("Received: " + input);             
			response = parse(input) + '\n';             
			outToClient.writeBytes(response);          
			}       
	}

	public static String parse(String received) {
		
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
