package tcp;

import java.io.*;
import java.net.*;

public class Client {

	public static String hostname;
	public static int portNumber;
	public static String message;
	
	public static void main(String[] args) throws IOException
	{
		if(args.length >= 4)
		{
			communication(args);
		}
		else
		{
			System.out.println("Usage: java Echo <hostname> <port_number> <oper> <opnd>");
			return;
		}
	}

	public static void communication(String[] args) throws IOException
	{
		hostname=args[0];
		portNumber=Integer.parseInt(args[1]);
		String temp = String.join(" ", args);
		message=get_message(temp);
		 
		String response;   
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
		Socket clientSocket = new Socket(hostname, portNumber);   
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
		   
		outToServer.writeBytes(message + '\n');   
		response = inFromServer.readLine();   
		System.out.println("FROM SERVER: " + response);   
		clientSocket.close(); 
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
