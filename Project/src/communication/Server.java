package communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import parse_files.SingleFile;

public class Server {
	
	private static String senderId="Server";
	private static String version = "1.0";
	private static char CRLF[] = {0xD,0xA};
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CloneNotSupportedException {
		
		if(args.length == 1 && isInteger(args[0])) {
			

			int port_number=Integer.parseInt(args[0]);
			TCP_Server tcp = new TCP_Server();
			
			String messageType=tcp.communication(port_number);
			SingleFile file=tcp.getFile();
			send_message(messageType, file);
			
			return;
		}
		System.out.println("Usage: java Echo <port_number>");
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
}
