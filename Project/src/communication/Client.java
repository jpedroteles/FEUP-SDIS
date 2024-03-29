package communication;

import java.io.IOException;

import tcp.TCP_Client;


public class Client {

	/** Inicializa a comunicacao tcp do cliente
	* @param args argumentos utilizar
 	*/
	public static void main(String[] args) throws IOException {
		
		if(valid_message(args)) {
			TCP_Client tcp = new TCP_Client();
			tcp.communication(args);
		}
		else
		{
			System.out.println("Usage: java Echo <peer_ap> <sub_protocol> <opnd_1> <opnd_2>");
			return;
		}
	}
	
	
	/** Faz a verificacao dos argumentos
	* @param args argumentos a verificar
	* @return true se a mensagem for valida false se nao o for
 	*/
	public static boolean valid_message(String[] args) {
		
		if(args.length < 3) {
			System.out.println("Invalid number of arguments: " + args.length);
			return false;
		}	
		if(!isInteger(args[0])) {
			System.out.println("PortNumber is not of Integer type: " + args[0]);
			return false;
		}
		else if(!isProtocol(args[1])) {
			System.out.println("Invalid Protocol type: " + args[1]);
			return false;
		}
		else if(args.length != 3) {
			if(!isInteger(args[3])) {
				System.out.println("Replication Degree is not of Integer type: " + args[3]);
				return false;
			}
		} 
		return true;
	}

	/** Verifica se a string é um inteiro
	* @param s string a verificar
	* @return true se a string for inteiro false se nao o for
 	*/
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
	
	/** Verifica se e um protocolo aceite pelo programa
	* @param s string a verificar
	* @return true se a string for inteiro false se nao o for
 	*/
	public static boolean isProtocol(String s) {
		if(s.equals("BACKUP") || s.equals("RESTORE") || s.equals("DELETE") || s.equals("RECLAIM")) {
			return true;
		}
		return false;
	}
			
}
