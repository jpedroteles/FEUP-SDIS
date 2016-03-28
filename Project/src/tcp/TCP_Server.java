package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import parser.FileProcessor;
import parser.ParseMessage;
import parser.SingleFile;
import udp.Multicast_DataBackup;
import udp.SendRequest;

public class TCP_Server implements Runnable {

	SingleFile file = new SingleFile();
	private static int port_number = 8080;
	private static String senderId;
	private static String version = "1.0";
	private static char crlf[] = {0xD,0xA};
	public static Thread thread1;

	public TCP_Server(String senderID) {
		senderId=senderID;
		thread1 = new Thread(this, "Thread1 created");
		thread1.start();
	}

	public String communication(int port_number) throws IOException, NoSuchAlgorithmException, CloneNotSupportedException {       

		String input;
		String response;
		ServerSocket welcomeSocket = new ServerSocket(port_number);          
		while(true){

			Socket connectionSocket = welcomeSocket.accept();             
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
			input = inFromClient.readLine();             
			//System.out.println("Received: " + input);             
			response = processor(input) + '\n';             
			outToClient.writeBytes(response);
			if(response.equals("BACKUP PROTOCOL\n") || response.equals("RESTORE PROTOCOL\n") || response.equals("DELETE PROTOCOL\n") || response.equals("RECLAIM PROTOCOL\n")) {
				break;
			}
			connectionSocket.close();
		}
		welcomeSocket.close();
		return response;
	}

	public String processor(String received) throws NoSuchAlgorithmException, CloneNotSupportedException, IOException {

		FileProcessor fp = new FileProcessor();
		String[] temp=get_message(received).split(" ");
		String ret=new String();

		//System.out.println("File name: " + temp[1]);
		String fileId = fp.get_fileId(temp[1]);
		//System.out.println("FileId: " + fileId);
		file.setFileId(fileId);
		int maxSize = 64*1000;
		ArrayList<byte[]> chunk_content = fp.divide_in_chunks(temp[1], maxSize);
		for(int i=0;i<chunk_content.size();i++) {
			file.addChunk(chunk_content.get(i));
		}
		//System.out.println(file.getChunks().size());
		if(temp.length == 3) {
			file.setReplicationDegree(Integer.parseInt(temp[2]));
		}
		//fp.create_chunk_folder();
		//fp.write_chunks(file);

		switch(temp[0]){
		case("BACKUP"):ret="BACKUP PROTOCOL";break;
		case("RESTORE"):ret="RESTORE PROTOCOL";break;
		case("DELETE"):ret="DELETE PROTOCOL";break;
		case("RECLAIM"):ret="RECLAIM PROTOCOL";break;
		default:ret="ERROR";break;
		}

		return ret;

	}

	public String get_message(String s) {

		String ret=new String();
		for(int i=0; i<s.length(); i++) {

			if(s.charAt(i)=='\0') {
				return ret;
			}
			ret+=(s.charAt(i));
		}
		return ret;
	}

	public static void send_message(String type, SingleFile file) throws IOException {

		SendRequest send = new SendRequest();
		String messageType=new String();
		String fileId=file.getFileId();

		int mc_port=8885;
		String mc_address = "225.0.0.1";
		int mdb_port=8887;
		String mdb_address = "225.0.0.3";

		if(type.equals("BACKUP PROTOCOL\n")) {
			messageType="PUTCHUNK";
		}
		else if(type.equals("RESTORE PROTOCOL\n")) {
			messageType="GETCHUNK";
		}
		else if(type.equals("DELETE PROTOCOL\n")) {
			messageType="DELETE";
		}
		else if(type.equals("RECLAIM PROTOCOL\n")) {
			messageType="REMOVED";
		}
		else {
			System.out.println("MessageType error: " + type);
			return;
		}
		for(int i=0; i<file.getChunks().size(); i++) {
			//System.out.println(messageType + " " + version + " " + senderId + " " + fileId + " " + file.getChunks().get(i).getChunkId() + " " + file.getChunks().get(i).getReplicationDegree() + " " + "CRLF");
			ParseMessage msg = new ParseMessage();
			byte[] header = msg.header(messageType, version, senderId, fileId, file.getChunks().get(i).getChunkId(), file.getChunks().get(i).getReplicationDegree(), crlf);
			byte[] message = msg.merge(header, file.getChunks().get(i).getContent());
			
			switch(messageType){

			case("PUTCHUNK"):send.sendRequest(message, mdb_port, mdb_address);break;
			case("GETCHUNK"):send.sendRequest(message, mc_port, mc_address);break;
			case("DELETE"):send.sendRequest(message, mc_port, mc_address);break;
			case("REMOVED"):send.sendRequest(message, mc_port, mc_address);break;
			}
		}	
	}

	public static boolean isInteger(String s) {
		try { 
			Integer.parseInt(s); 
		} catch(NumberFormatException e) { 
			return false; 
		} catch(NullPointerException e) {
			return false;
		}
		return true;
	}

	public void run() {

		while(true) {
			//System.out.println("TCP Thread is running");
			try {
				String messageType=communication(port_number);
				//System.out.println("Message from client recieved...");
				send_message(messageType, file);
				Thread.sleep(1000);
			}
			catch(InterruptedException | NoSuchAlgorithmException | IOException | CloneNotSupportedException e) {
				//System.out.println("TCP Catch");
				break;
			}
			//System.out.println("TCP End");
		}
	}
}
