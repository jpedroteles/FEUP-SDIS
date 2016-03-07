package multicast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.*;

public class Server {
	
	public static String srvc_addr;
	public static int srvc_port;
	public static String mcast_addr;
	public static int mcast_port;
	public static Data_base db;
	public static MulticastSocket multSocket;
	public static String message;
	public static DatagramSocket newServerSocket;
	
	//8080 224.0.0.3 8000
	public static void main(String[] args) throws IOException{
		
		db=new Data_base();
		String input=get_args();
		System.out.println("INPUT: " + input);

		String[] input_args = input.split(" ");
		
		if(input_args.length != 3) {
			System.out.println("Invalid number of arguments!");
			return;
		}

		
		//values from args
		srvc_port=Integer.parseInt(input_args[0]);
		mcast_addr=input_args[1];
		mcast_port=Integer.parseInt(input_args[2]);
		srvc_addr="localhost";

		multSocket= new MulticastSocket();
		multSocket.setTimeToLive(1);
		
		//datagram server
		DatagramSocket serverSocket = new DatagramSocket(srvc_port);

		//service
		//message = serverSocket.getLocalAddress().getHostAddress()+" : "+serverSocket.getLocalPort();
		message = srvc_addr + ":" + srvc_port;
		System.out.println("MESSAGE: " + message);
		service(InetAddress.getByName(mcast_addr));
		
		runService(serverSocket);
		
		serverSocket.close();
		multSocket.close();
	}

	private static void service(InetAddress multAddress) throws SocketException {
		
		newServerSocket = new DatagramSocket();
		TimerTask broadcast = new TimerTask() {
			@Override
			public void run() {
				
				DatagramPacket packet = new DatagramPacket(message.getBytes(), message.getBytes().length, multAddress, mcast_port);
				try {
					newServerSocket.send(packet);
					//System.out.println("Multicast: " + multAddress + ":" + mcast_port + " - " + message);
				} catch (IOException e) {
					//System.out.println("Error while broadcasting service! " + e.getMessage());
					return;
				}
			}
		};

		Timer broadcastTimer = new Timer("BroadcastService");
		broadcastTimer.scheduleAtFixedRate(broadcast, 0, 10);
		
	}


	private static void runService(DatagramSocket serverSocket) throws IOException {
		// Map with all plates	
		String request, response, requestArgs[], operation;

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
						if(db.register(ownerName, plateNumber) == -1) {
							response = "-1";
						} else {
							response = "" + db.count_cars();
						}
					}
				} else
					// Lookup vehicle
					if(operation.equalsIgnoreCase("LOOKUP")) {
						String plateNumber = requestArgs[1];
						response=db.lookup(plateNumber);
					} else {
						response = "-1";
					}
			}
			InetAddress hostAddress = receiverPacket.getAddress();
			srvc_port = receiverPacket.getPort();
			System.out.println("RESPONSE: " + response);
			senderData=response.getBytes();
			System.out.println("RESPONSE: " + senderData);
			DatagramPacket sendPacket = new DatagramPacket(senderData, senderData.length, hostAddress, srvc_port);
			serverSocket.send(sendPacket);

			}
		}
	
	private static String get_args()
	{
		 Scanner scanner = new Scanner(System.in);
         String ret;
         ret=scanner.nextLine();
         return ret;
	}
}
