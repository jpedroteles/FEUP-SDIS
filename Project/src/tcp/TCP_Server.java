package tcp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import parser.FileProcessor;
import parser.ParseLog;
import parser.ParseMessage;
import parser.SingleFile;
import protocols.History;
import udp.Multicast_DataBackup;
import udp.SendRequest;

public class TCP_Server implements Runnable {

	SingleFile file = new SingleFile();
	private static int port_number;
	private static String senderId;
	private static String version = "1.0";
	private static char crlf[] = {0xD,0xA};
	public static Thread thread1;
	public static int space=0;
	public static History history = new History();
	public static String filename;
	public static int mc_port;
	public static String mc_address;
	public static int mdb_port;
	public static String mdb_address;

	/** Construtor da classe TCP_Server, esta classe da ligacao tcp do servidor
	* @param  senderID  Identificador do servidor
	* @param  mc_a adresso do canal de controlo
	* @param  mc_p port do canal de controlo
	* @param  mdb_a adresso do canal multicast de backup
	* @param  mdb_p port do canal multicast de backup
	* @param  port port do canal
 	*/
	public TCP_Server(String senderID, String mc_a, int mc_p, String mdb_a, int mdb_p, int port) {
		port_number=port;
		mc_port=mc_p;
		mc_address=mc_a;
		mdb_port=mdb_p;
		mdb_address=mdb_a;
		senderId=senderID;
		thread1 = new Thread(this, "Thread1 created");
		thread1.start();
	}

	/** Cria a socket a ser utilizada na comunicacao
	* @param  port_number  port do canal
	* @return a mensagem recebida pela socket criada
 	*/
	public String communication(int port_number) throws IOException, NoSuchAlgorithmException, CloneNotSupportedException {       

		String input;
		String response;
		ServerSocket welcomeSocket = new ServerSocket(port_number);          
		while(true){

			Socket connectionSocket = welcomeSocket.accept();             
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));             
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());             
			input = inFromClient.readLine();                       
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

	/** Processa a mensagem que recebe
	* @param  received  string a processar
	* @return ret o protocolo a ser utilizado
 	*/
	public String processor(String received) throws NoSuchAlgorithmException, CloneNotSupportedException, IOException {

		FileProcessor fp = new FileProcessor(senderId);
		String[] temp=get_message(received).split(" ");
		String ret=new String();

		filename=temp[1];
		String fileId = fp.get_fileId(temp[1]);
		file.setFileId(fileId);
		int maxSize = 64*1000;

		switch(temp[0]){
		case("BACKUP"):{

			if(temp.length == 2) {
				file.setReplicationDegree(1);
			}
			else{
				file.setReplicationDegree(Integer.parseInt(temp[2]));
			}

			ArrayList<byte[]> chunk_content = fp.divide_in_chunks(temp[1], maxSize);
			for(int i=0;i<chunk_content.size();i++) {
				file.addChunk(chunk_content.get(i));
			}
			System.out.println("NUM CHUNKS: " + file.getChunks().size());
			ret="BACKUP PROTOCOL";break;}
		case("RESTORE"):ret="RESTORE PROTOCOL";break;
		case("DELETE"):ret="DELETE PROTOCOL";break;
		case("RECLAIM"):{
			space=Integer.parseInt(temp[1]);
			ArrayList<byte[]> chunk_content = fp.getMyChunks();
			for(int i=0;i<chunk_content.size();i++) {
				file.addChunk(chunk_content.get(i));
			}
			System.out.println("CHUNKS");
			ret="RECLAIM PROTOCOL";break;}
		default:ret="ERROR";break;
		}

		return ret;

	}

	/** Retorna uma mensagem
	* @param  s  string com todos os argumentos
	* @return ret string com a mensagem
 	*/
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

	/** Envia uma mensagem com o protocolo a ser utilizado  e o ficheiro afetado por ele
	* @param  type  string o tipo de protocolo a ser chamado
	* @param file nome do ficheiro sobre o qual vai ser utilizado o protocolo
 	*/
	public static void send_message(String type, SingleFile file) throws IOException, InterruptedException {

		SendRequest send = new SendRequest();
		String messageType=new String();
		String fileId=file.getFileId();		

		if(type.equals("BACKUP PROTOCOL\n")) {
			messageType="PUTCHUNK";
		}
		else if(type.equals("RESTORE PROTOCOL\n")) {
			messageType="GETCHUNK";
			createFolder();
		}
		else if(type.equals("DELETE PROTOCOL\n")) {
			messageType="DELETE";
		}
		else if(type.equals("RECLAIM PROTOCOL\n")) {
			messageType="RECLAIM";
		}
		else {
			System.out.println("MessageType error: " + type);
			return;
		}

		if(messageType.equals("PUTCHUNK")){
			for(int i=0; i<file.getChunks().size(); i++) {

				ParseMessage msg = new ParseMessage();
				ParseLog pl = new ParseLog();
				byte[] header = msg.header(messageType, version, senderId, fileId, file.getChunks().get(i).getChunkId(), file.getReplicationDegree(), crlf);
				byte[] message = msg.merge(header, file.getChunks().get(i).getContent());
				
				send.sendRequest(message, mdb_port, mdb_address, senderId);
				history.add(filename, fileId, Integer.toString(file.getChunks().get(i).getChunkId()), senderId, messageType, "SENT");
				System.out.println("REPLICATION DEGREE: " + pl.countRepDegree(fileId, Integer.toString(file.getChunks().get(i).getChunkId()), senderId));
			}
		}
		else if(messageType.equals("DELETE")){
			ParseMessage msg = new ParseMessage();
			byte[] header = msg.header(messageType, version, senderId, fileId, 0, 0, crlf);
			send.sendRequest(header, mc_port, mc_address,senderId);
			history.add(filename, fileId, "-", senderId, messageType, "SENT");

		}
		else if(messageType.equals("RECLAIM")){
			ParseMessage msg = new ParseMessage();
			FileProcessor fp = new FileProcessor(senderId);

			while(space > 0){
				fileId=fp.getBestCandidate(fp.getChunkNames());
				String reply = "REMOVED " + version + " " + senderId + " " + fileId + " " + fp.getNum(fp.getBestIndex(fp.getChunkNames())) + " " + crlf[0]+crlf[1]+crlf[0]+crlf[1];

				byte[] header = reply.getBytes();
				space-=fp.getFileSize(fileId+"-"+fp.getNum(fp.getBestIndex(fp.getChunkNames()))+".bin");
				send.sendRequest(header, mc_port, mc_address, senderId);

			}
			history.add("-", "-", "-", senderId, messageType, "SENT");

		}
		else if(messageType.equals("GETCHUNK")){
			FileProcessor fp = new FileProcessor(senderId);
			int[] pos = fp.getPos(fp.getChunkNums());
			for(int i=0; i<fp.getChunkNums().size(); i++) {
				ParseMessage msg = new ParseMessage();
				byte[] header = msg.header(messageType, version, senderId, fp.getChunkNames().get(i),  pos[i], 0, crlf);
				send.sendRequest(header, mc_port, mc_address, senderId);	
				history.add(filename, fp.getChunkNames().get(i), Integer.toString(pos[i]), senderId, messageType, "SENT");
			}
			changeFilename(fileId);
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
				send_message(messageType, file);
				Thread.sleep(1000);
			}
			catch(InterruptedException | NoSuchAlgorithmException | IOException | CloneNotSupportedException e) {
				break;
			}
		}
	}
	
	
	public static void createFolder(){
		File folder = new File("restoredFiles");

		if (!folder.exists())
		{
			folder.mkdir();
		}
	}
	
	public static void changeFilename(String filename) throws IOException{
		
		ParseLog pl = new ParseLog();
		
		File folder = new File("restoredFiles");
		
		File[] files = folder.listFiles();

		for(int i=0;i<files.length;i++){
			if(files[i].getName().equals(filename)){
				System.out.println("YES: "+pl.getFile(filename));
				files[i].renameTo(new File(pl.getFile(filename)));
			}
		}		
	}
}
