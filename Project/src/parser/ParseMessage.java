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
		
		//System.out.println(new String(ret));
		
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
		
		//System.out.println("HEADER: " + parts[0]);
		return parts[0];
	}
	
	public byte[] getContent(DatagramPacket packet, char[] crlf) {
		
		String split = "" + crlf[0] + crlf[1] + crlf[0] + crlf[1];
		String temp=new String(packet.getData(), 0, packet.getLength());
		String[] parts = temp.split(split);
		
		//System.out.println("CONTENT: " + parts[1]);
		
		byte[] ret=parts[1].getBytes();
		return ret;
	}

}
