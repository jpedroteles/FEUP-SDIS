package communication;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Server {
	
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CloneNotSupportedException {
		
		if(args.length == 1 && isInteger(args[0])) {
			

			int port_number=Integer.parseInt(args[0]);
			TCP_Server tcp = new TCP_Server();
			
			tcp.communication(port_number);
			return;
		}
		System.out.println("Usage: java Echo <port_number>");
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
