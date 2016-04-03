package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_DataRestore implements Runnable {
	/** Thread de tratamento do protocolo */
	public static Thread mdr_thread;
	/** Numero de porta multicast restore*/
	public static int mdr_port;
	/** Adereco de porta multicast restore*/
	public static String mdr_address;
	/** Socket multicast restore*/
	public static MulticastSocket mdr;
	/** Adereco de porta multicast control*/
	public static InetAddress mdrAddress;
	/** Identificador do servidor*/
	public String ServerID;
	/** Parse da mensagem*/
	public ParseMessage pm = new ParseMessage();
	/** Flag de fim*/
	private static char crlf[] = {0xD,0xA};
	/** Novo historico*/
	public History hist = new History();


	/** Inicializa o multicast de data restore
	* @param ServerId mensagem a ser enviada
	* @param mdr_a adereco do canal restore
	* @param mdr_p porta do canal restore
 	*/
	public Multicast_DataRestore(String ServerId, String mdr_a, int mdr_p) throws IOException{
		mdr_port=mdr_p;
		mdr_address=mdr_a;
		System.out.println(mdr_a+"--"+mdr_p);
		ServerID = ServerId;
		mdr = new MulticastSocket(mdr_port);
		mdrAddress = InetAddress.getByName(mdr_address);
		mdr.joinGroup(mdrAddress);
		mdr.setLoopbackMode(true);
		mdr_thread = new Thread(this, "mdr_Thread created");
		mdr_thread.start();
	}

	/** Recebe uma mensagem do canal separa em header e conteudo, chama o processamento respetivo e adiciona ao historico
 	*/
	public void mdr_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		
		mdr.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		
		hist.add("-", pm.getId(header), pm.getChunkNum(header), pm.getSenderId(header), pm.getMessageType(header), "RECEIVED");

		System.out.println(header);
		
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID, mdr_port, mdr_address);
	}


	/** Thread que gera o canal de comunicacao
 	*/
	public void run() {
		
		while(true) {
			try {
				mdr_communication();
			}
			catch(IOException e) {
				break;
			}
		}
		
		try {
			mdr.leaveGroup(mdrAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mdr.close();
	}
}


