

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Server {
	public static void main(String[] args) throws IOException{
		int port, multPort;
		String multAddress, message;
		if(args.length < 3) {
			System.out.println("Invalid number of arguments!");
			return;
		}

		//values from agrs
		port=Integer.parseInt(args[0]);
		multAddress=args[1];
		multPort=Integer.parseInt(args[2]);

		//datagram server
		DatagramSocket serverSocket;
		serverSocket = new DatagramSocket(port,InetAddress.getByName("127.0.0.1"));

		//service
		message = serverSocket.getLocalAddress().getHostAddress()+" : "+serverSocket.getLocalPort();
		service(InetAddress.getByName(multAddress), multPort, message);
		
		runService(serverSocket);


	}

	private static void service(InetAddress multAddress, int multPort, String message) throws SocketException {
		DatagramSocket serverSocket = new DatagramSocket();
		TimerTask broadcast = new TimerTask() {
			@Override
			public void run() {
				DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, multAddress, multPort);
				try {
					serverSocket.send(packet);
					System.out.println("Multicast: " + multAddress + ":" + multPort + " - " + message);
				} catch (IOException e) {
					System.out.println("Error while broadcasting service! " + e.getMessage());
				}
			}
		};

		Timer broadcastTimer = new Timer("BroadcastService");
		broadcastTimer.scheduleAtFixedRate(broadcast, 0, 10);
	}


	private static void runService(DatagramSocket serverSocket) throws IOException {
		// Map with all plates
		Map<String, String> plates = new HashMap<>();	
		String request, response, requestArgs[], operation;
		int port;

		//receive data
		byte [] receiverData, senderData;
		while(true){
			receiverData = new byte [1024];
			DatagramPacket receiverPacket = new DatagramPacket(receiverData, receiverData.length);
			serverSocket.receive(receiverPacket);
			//treat request
			request= new String(receiverPacket.getData()).trim();
			requestArgs=request.split(" ");
			operation=requestArgs[0].toUpperCase();

			if(requestArgs.length < 2) {
				response = "-1";
			} else {
				if(operation.equalsIgnoreCase("REGISTER")) {
					if(requestArgs.length < 3) {
						response = "-1";
					} else {
						String plateNumber = requestArgs[1],
								ownerName = requestArgs[2];
						for(int i = 3; i < requestArgs.length; i++)
							ownerName += " " + requestArgs[i];
						if(plates.containsKey(plateNumber)) {
							response = "-1";
						} else {
							plates.put(plateNumber, ownerName);
							response = "" + plates.size();
						}
					}
				} else
					// Lookup vehicle
					if(operation.equalsIgnoreCase("LOOKUP")) {
						String plateNumber = requestArgs[1];
						if(plates.containsKey(plateNumber)) {
							response = plateNumber + " " + plates.get(plateNumber);
						} else {
							response = "-1";
						}
					} else {
						response = "-1";
					}
			}
			InetAddress hostAddress = receiverPacket.getAddress();
			port = receiverPacket.getPort();
			senderData = response.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(senderData, senderData.length, hostAddress, port);
			serverSocket.send(sendPacket);

			}
		}
	}
