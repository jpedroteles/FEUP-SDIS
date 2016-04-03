package parser;

import java.net.DatagramPacket;
import java.util.Arrays;

public class ParseMessage {

	public byte[] header(String messageType, String version, String senderId, String fileId, int chunkId, int ReplicationDegree, char[] crlf){
		
		byte[] msgType = messageType.getBytes();
		byte[] vrs = version.getBytes();
		byte[] sId = senderId.getBytes();
		byte[] fId = fileId.getBytes();
		byte[] cId = Integer.toString(chunkId).getBytes();
		byte[] rpDegree = Integer.toString(ReplicationDegree).getBytes();
		byte[] end = new String(crlf).getBytes();
		byte[] space = " ".getBytes();
		
		byte[] ret1 = merge(msgType, space);
		byte[] ret2 = merge(ret1, vrs);
		byte[] ret3 = merge(ret2, space);
		byte[] ret4 = merge(ret3, sId);
		byte[] ret5 = merge(ret4, space);
		byte[] ret6 = merge(ret5, fId);
		byte[] ret7 = merge(ret6, space);
		byte[] ret8 = merge(ret7, cId);
		byte[] ret9 = merge(ret8, space);
		byte[] ret10 = merge(ret9, rpDegree);
		byte[] ret11 = merge(ret10, space);
		byte[] ret12 = merge(ret11, end);
		byte[] ret = merge(ret12, end);
		
		
		return ret;
	}
	
	public byte[] merge(byte[] header, byte[] content){
		
		byte[] ret = new byte[header.length + content.length];
		System.arraycopy(header, 0, ret, 0, header.length);
		System.arraycopy(content, 0, ret, header.length, content.length);
		
		return ret;
	}

	public String getHeader(DatagramPacket packet, char[] crlf) {
		
		String split = "" + crlf[0] + crlf[1];
		String temp=new String(packet.getData(), 0, packet.getLength());
		String[] parts = temp.split(split);
		
		return parts[0];
	}
	
	public byte[] getContent(DatagramPacket packet, char[] crlf) {
		
		byte[] temp=packet.getData();
		
		return content(temp, crlf);
	}
	
	public String getId(String header) {
		String[] split = header.split(" ");
		return split[3];
	}
	
	public String getMessageType(String header) {
		String[] split = header.split(" ");
		return split[0];
	}
	
	public String getChunkNum(String header) {
		String[] split = header.split(" ");
		return split[4];
	}
	
	public String getVersion(String header) {
		String[] split = header.split(" ");
		return split[1];
	}
	
	public String getSenderId(String header) {
		String[] split = header.split(" ");
		return split[2];
	}
	
	public byte[] content(byte[] message, char[] crlf){
		boolean flag=false;
		int j=0;
		byte[] ret=null;
		for(int i=0;i<message.length-4;i++){
			if(message[i]==(new String(crlf).getBytes()[0]) && message[i+1]==(new String(crlf).getBytes()[1]) && message[i+2]==(new String(crlf).getBytes()[0]) && message[i+3]==(new String(crlf).getBytes()[1]) && flag==false){
				flag=true;
				ret = new byte[message.length-i];
				i+=4;
			}
			
			if(flag){
				ret[j]=message[i];
				j++;
			}
		}
		return ret;
	}

}
