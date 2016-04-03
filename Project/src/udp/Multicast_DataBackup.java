package udp;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import parser.ParseMessage;
import protocols.History;

public class Multicast_DataBackup implements Runnable {

	/** Thread de tratamento do protocolo */
	public static Thread mdb_thread;
	/** Numero de porta multicast backup*/
	public static int mdb_port;
	/** Adereco de porta multicast backup*/
	public static String mdb_address;
	/** Numero de porta multicast control*/
	public static int mc_port;
	/** Adereco de porta multicast control*/
	public static String mc_address;
	/** Socket multicast backup*/
	public static MulticastSocket mdb;
	/** Adereco de porta multicast control*/
	public static InetAddress mdbAddress;
	/** Parse da mensagem*/
	public ParseMessage pm = new ParseMessage();
	/** Flag de fim*/
	private static char crlf[] = {0xD,0xA};
	/** Identificador do servidor*/
	public String ServerID;
	/** Novo historico*/
	public History hist = new History();

	/** Inicializa o multicast de backup
	* @param ServerId mensagem a ser enviada
	* @param mdb_a adereco do canal backup
	* @param mdb_p porta do canal backup
	* @param mc_a adereco do canal controlo
	* @param mc_p porta do canal controlo
 	*/
	public Multicast_DataBackup(String ServerId, String mdb_a, int mdb_p, String mc_a, int mc_p) throws IOException{
		mdb_port=mdb_p;
		mdb_address=mdb_a;
		mc_port=mc_p;
		mc_address=mc_a;
		ServerID = ServerId;
		mdb = new MulticastSocket(mdb_port);
		mdbAddress = InetAddress.getByName(mdb_address);
		mdb.joinGroup(mdbAddress);
		mdb.setLoopbackMode(true);
		mdb_thread = new Thread(this, "mdb_Thread created");
		mdb_thread.start();
	}

	/** Recebe uma mensagem do canal separa em header e conteudo, chama o processamento respetivo e adiciona ao historico
 	*/
	public void mdb_communication() throws IOException{

		byte[] data= new byte[65536];
		DatagramPacket packet = new DatagramPacket(data, 65536);

		mdb.receive(packet);
		String header = pm.getHeader(packet, crlf);
		byte[] content = pm.getContent(packet, crlf);
		hist.add("-", pm.getId(header), pm.getChunkNum(header), pm.getSenderId(header), pm.getMessageType(header), "RECEIVED");
		

		System.out.println(header);
		
		MessageProcessor msg_pro = new MessageProcessor(header, content, ServerID, mc_port, mc_address);
		
	}

	/** Thread que gera o canal de comunicacao
 	*/
	public void run() {
		
		while(true) {
			try {
				mdb_communication();
			}
			catch(IOException e) {
				break;
			}
		}
		
		try {
			mdb.leaveGroup(mdbAddress);
		} catch (IOException e) {
			e.printStackTrace();
		}
		mdb.close();
	}
}


