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

public class TCP_Server {

	public void communication(int port_number) throws IOException, NoSuchAlgorithmException, CloneNotSupportedException {       

		String input;
		String response;
		ServerSocket welcomeSocket = new ServerSocket(port_number);          
		while(true){

			Socket connectionSocket = welcomeSocket.accept();             
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
			input = inFromClient.readLine();             
			System.out.println("Received: " + input);             
			response = processor(input) + '\n';             
			outToClient.writeBytes(response);
			if(response.equals("BACKUP PROTOCOL\n") || response.equals("RESTORE PROTOCOL\n") || response.equals("DELETE PROTOCOL\n") || response.equals("RECLAIM PROTOCOL\n")) {
				break;
			}
		}       
	}

	public String processor(String received) throws NoSuchAlgorithmException, UnsupportedEncodingException, CloneNotSupportedException {

		FileProcessor fp = new FileProcessor();
		SingleFile file = new SingleFile();
		String[] temp=get_message(received).split(" ");
		String ret=new String();
		
		System.out.println("File name: " + temp[1]);
		String fileId = fp.get_fileId(temp[1]);
		System.out.println("FileId: " + fileId);
		file.setFileId(fileId);
		int maxSize = 64*8*1000;
		ArrayList<String> chunk_content = fp.get_chunks(temp[1], maxSize);
		
		for(int i=0;i<chunk_content.size();i++) {
			file.addChunk(chunk_content.get(i));
		}
		
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
}
