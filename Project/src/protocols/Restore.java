package protocols;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
 


import parser.ParseMessage;
import udp.SendRequest;
import protocols.Utils;

public class Restore {
    int mdr_port=8886;
    String mdr_address = "225.0.0.2";
    private static char crlf[] = {0xD,0xA};
 
    public Restore (String header, String serverId) throws IOException {
 
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
 
        for(int i=0; i<files.length;i++){
            if(pm.getId(header).equals(getFileId(files[i])) && pm.getChunkNum(header).equals(getChunkNum(files[i]))){
            	System.out.println("RESTORED1");
            	Path path = Paths.get("chunks/"+pm.getId(header)+"-"+pm.getChunkNum(header)+".bin");
                temp = Files.readAllBytes(path);
                data=pm.merge(data,temp);
                send.sendRequest(data, mdr_port, mdr_address);
                System.out.println("RESTORED2");
                hist.add("-", pm.getId(header), pm.getChunkNum(header), serverId, "CHUNK", "SENT");
            }
        }
        
    }
 
    public String getFileId(File files){
        String[] split=files.getName().split("-");
        return split[0];
    }
 
    public String getChunkNum(File files){
        String[] split=files.getName().split("-");
        String[] ret=split[1].split(".bin");
		return ret[0];
    }
}