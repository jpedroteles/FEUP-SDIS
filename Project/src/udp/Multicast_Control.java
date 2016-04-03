package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_Control implements Runnable {
	
	/** Thread de tratamento do protocolo */
	public static Thread mc_thread;
	/** Numero de porta multicast control*/
	public static int mc_port;
	/** Adereco de porta multicast control*/
	public static String mc_address;
	/** Numero de porta multicast restore*/
	public static int mdr_port;
	/** Adereco de porta multicast restore*/
	public static String mdr_address;
	/** Socket multicast control*/
	public static MulticastSocket mc;
	/** Adereco de porta multicast control*/
	public static InetAddress mcAddress;
	/** Parse da mensagem*/
	public ParseMessage pm = new ParseMessage();
	/** Flag de fim*/
	private static char crlf[] = {0xD,0xA};
	/** Identificador do servidor*/
	public String ServerID;
	/** Novo historico*/
	History hist = new History();

	/** Inicializa o multicast de controlo
	* @param ServerId mensagem a ser enviada
	* @param mc_a adereco do canal controlo
	* @param mc_p porta do canal controlo
	* @param mdr_a adereco do canal restore
	* @param mdr_p porta do canal restore
 	*/
	public Multicast_Control(String ServerId, String mc_a, int mc_p, String mdr_a, int mdr_p) throws IOException{
		mc_port=mc_p;
		mc_address=mc_a;
		mdr_port=mdr_p;
		mdr_address=mdr_a;
		ServerID = ServerId;
		mc = new MulticastSocket(mc_port);
		mcAddress = InetAddress.getByName(mc_address);
		mc.joinGroup(mcAddress);
		mc.setLoopbackMode(true);
		mc_thread = new Thread(this, "mc_Thread created");
		mc_thread.start();
	}

	/** Recebe uma mensagem do canal separa em header e conteudo, chama o processamento respetivo e adiciona ao historico
 	*/
	public void mc_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		mc.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		if(pm.getMessageType(header).equals("DELETE")){

			hist.add("-", pm.getId(header), "-", ServerID, pm.getMessageType(header), "RECEIVED");
		}
		else{

			hist.add("-", pm.getId(header), pm.getChunkNum(header), ServerID, pm.getMessageType(header), "RECEIVED");
		}

		System.out.println(header);
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID, mdr_port, mdr_address);
	}

	/** Thread que gera o canal de comunicacao
 	*/
	public void run() {
		
		while(true) {
			try {
				mc_communication();
			}
			catch(IOException e) {
				break;
			}
		}
		
		try {
			mc.leaveGroup(mcAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mc.close();
	}
}


