

import java.io.IOException;
import java.net.*;

public class Cliente {

	public static void main(String[] args) throws IOException {
		//Check if arg number is correct
		if(args.length < 4) {
			System.out.println("Invalid number of arguments!");
			return;
		}

		//Address and port
		String multicastAddress= args[0];
		int port= Integer.parseInt(args[1]);

		//lookup service address
		String service=lookupService(InetAddress.getByName(multicastAddress), port);

		//Response treatment
		String[] responseArgs = service.trim().split(":");
		InetAddress responseAddress = InetAddress.getByName(responseArgs[0]);
		int responsePort = Integer.parseInt(responseArgs[1]);

		System.out.println("Multicast: " + multicastAddress + ":" + port + " - " + 
				responseAddress + ":" + responsePort);

		String[] requestArgs = new String[3];
		for(int i = 2; i < args.length; i++)
			requestArgs[i - 2] = args[i];
		createRequest(responseAddress, responsePort, requestArgs);
	}

	private static String createRequest(InetAddress address, int port, String[] args) throws IOException {
		// TODO Auto-generated method stub
		String operation= args[0];
		String clientRequest , plateNumber, ownerName;
		if(operation.equalsIgnoreCase("register")){
			if(args.length < 3){
				System.out.println("Invalid number of arguments");
				return "";
			}
			plateNumber=args[1];
			ownerName= args[2];

			clientRequest = "REGISTER" +plateNumber +" "+ownerName;
		}
		else if(operation.equalsIgnoreCase("lookup")){
			plateNumber=args[1];
			ownerName=args[2];
			clientRequest="LOOKUP" +plateNumber+ " " + ownerName;
		}
		else
			return "";

		String response = sendRequest(address, port, clientRequest);
		System.out.println(operation+ " " + clientRequest + " ::  " + response);
		return response;

	}

	private static String sendRequest(InetAddress address, int port, String request) throws IOException {
		DatagramSocket clientSocket = new DatagramSocket();

		// Send the request
		byte[] requestData = request.getBytes();
		DatagramPacket sendPacket = new DatagramPacket(requestData, requestData.length, address, port);
		clientSocket.send(sendPacket);

		// Receive the request
		byte[] receivedData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receivedData, receivedData.length);
		clientSocket.receive(receivePacket);

		// Close client socket
		clientSocket.close();

		return new String(receivedData);

	}

	private static String lookupService(InetAddress address, int port) throws IOException {
		MulticastSocket multiSocket= new MulticastSocket(port);
		multiSocket.joinGroup(address);

		//Read from server
		byte[] buffer = new byte[256];
		DatagramPacket packet = new DatagramPacket(buffer,buffer.length);
		multiSocket.receive(packet);

		return new String(buffer,0,buffer.length);
	}
}
