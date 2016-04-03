package protocols;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
 




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

import parser.ParseMessage;
import udp.SendRequest;
import protocols.Utils;

public class Restore {
/** Numero de porta multicast restore */
    int mdr_port;
    /** adereco de porta multicast restore */
    String mdr_address;
    /** Flag de fim */
    private static char crlf[] = {0xD,0xA};
 
 /** Construtor da classe Restore esta classe trata de fazer o restore do chunk de um ficheiro
	* @param  header  header da mensagem
	* @param  content conteudo do chunk
	* @param  senderId identificador do cliente
	* @param  pt porta multicast
	* @param  a Adereco multicast
 	*/
    public Restore (String header, String serverId, int pt, String a) throws IOException, InterruptedException {
    	mdr_port=pt;
    	mdr_address=a;
        ParseMessage pm = new ParseMessage();
        Utils utils = new Utils();
        SendRequest send = new SendRequest();
        History hist = new History();
 
        byte[] data= new byte[65536];
        byte[] temp=null;
        String reply = "CHUNK " + pm.getVersion(header) + " " + serverId + " " + pm.getId(header) + " " + pm.getChunkNum(header) + " " + crlf[0]+crlf[1]+crlf[0]+crlf[1];
        data= reply.getBytes();
        utils.checkFolder();
 
        File folder = new File("chunks");
        File[] files = folder.listFiles();
        Arrays.sort(files);

		System.out.println(reply);
        for(int i=0; i<files.length;i++){
            if(pm.getId(header).equals(getFileId(files[i])) && pm.getChunkNum(header).equals(getChunkNum(files[i]))){
            	
            	Path path = Paths.get("chunks/"+pm.getId(header)+"-"+pm.getChunkNum(header)+".bin");
                temp = Files.readAllBytes(path);
                byte[] newData=pm.merge(data,temp);
                send.sendRequest(newData, mdr_port, mdr_address, serverId);
                hist.add("-", pm.getId(header), pm.getChunkNum(header), serverId, "CHUNK", "SENT");
            }
        }
        
    }
 
 	/** Retorna o identificador de um ficheiro
	* @param  file ficheiro do qual se quer o identificadro
	* @return split string com o identificador
 	*/
    public String getFileId(File files){
        String[] split=files.getName().split("-");
        return split[0];
    }
 
  	/** Retorna o numero de um chunk
	* @param  file ficheiro do qual se quer o numero
	* @return split string com o numero do chunk
 	*/
    public String getChunkNum(File files){
        String[] split=files.getName().split("-");
        String[] ret=split[1].split(".bin");
		return ret[0];
    }

}
