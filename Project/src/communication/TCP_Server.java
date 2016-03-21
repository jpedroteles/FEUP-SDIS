package communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import parse_files.FileProcessor;
import parse_files.SingleFile;

public class TCP_Server implements Runnable {

	SingleFile file = new SingleFile();
	private static int port_number = 8080;
	private static String senderId="Server";
	private static String version = "1.0";
	private static char CRLF[] = {0xD,0xA};
	static Thread thread1;
	
	TCP_Server() {
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

	public String processor(String received) throws NoSuchAlgorithmException, UnsupportedEncodingException, CloneNotSupportedException {

		FileProcessor fp = new FileProcessor();
		String[] temp=get_message(received).split(" ");
		String ret=new String();
		
		//System.out.println("File name: " + temp[1]);
		String fileId = fp.get_fileId(temp[1]);
		//System.out.println("FileId: " + fileId);
		file.setFileId(fileId);
		int maxSize = 64*8*1000;
		ArrayList<String> chunk_content = fp.get_chunks(temp[1], maxSize);
		
		for(int i=0;i<chunk_content.size();i++) {
			file.addChunk(chunk_content.get(i));
		}
		//System.out.println(file.getChunks().size());
		if(temp.length == 3) {
			file.setReplicationDegree(Integer.parseInt(temp[2]));
		}
		
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

	public static void send_message(String type, SingleFile file) {

		String messageType=new String();
		String fileId=file.getFileId();

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
			System.out.println(messageType + " " + version + " " + senderId + " " + fileId + " " + file.getChunks().get(i).getChunkId() + " " + file.getChunks().get(i).getReplicationDegree() + " " + "CRLF");
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
			try {
				String messageType=communication(port_number);
				System.out.println("Message from client recieved...");
				System.out.println("Sending message...");
				send_message(messageType, file);
				Thread.sleep(1000);
			}
			catch(InterruptedException | NoSuchAlgorithmException | IOException | CloneNotSupportedException e) {
		
				break;
			}
			break;
		}
	}
}
