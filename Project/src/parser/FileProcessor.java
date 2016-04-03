package parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

public class FileProcessor {
	
	public static String serverId;
	
	public FileProcessor(String s){
		serverId=s;
	}

	public String get_fileId(String filename) throws CloneNotSupportedException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		File file = new File(filename);
		filename+=file.lastModified();
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(filename.getBytes("UTF-8"));
		byte[] fileId = md.digest();

		return DatatypeConverter.printHexBinary(fileId);
	}

	public ArrayList<byte[]> divide_in_chunks(String filename, int maxSize) {

		File inputFile = new File(filename);
		FileInputStream inputStream;
		long fileSize = inputFile.length();
				
		int read=0, readLength = maxSize;
		byte[] chunkPart;
		boolean addLastChunk=false;
		ArrayList<byte[]> ret = new ArrayList<byte[]>();

		if((fileSize % maxSize) == 0) {
			addLastChunk=true;
		}

		try {
			inputStream = new FileInputStream(inputFile);
			while(fileSize > 0){

				if(fileSize <= maxSize){
					readLength = (int)fileSize;
				}
				chunkPart = new byte[readLength];
				read = inputStream.read(chunkPart,0,readLength);
				fileSize -= read;

				assert (read == chunkPart.length);

				ret.add(chunkPart);
								
				chunkPart=null;
			}
			inputStream.close();

		} catch (IOException exception){
			exception.printStackTrace();
		}

		if(addLastChunk) {
			byte[] s = new byte[maxSize];
			ret.add(s);
		}
		return ret;
	}

	public ArrayList<byte[]> getMyChunks() throws IOException{
		ArrayList<byte[]> ret = new ArrayList<byte[]>();

		File folder = new File("chunks");
		File[] files = folder.listFiles();

		for(int i=0; i<files.length; i++){
			
			Path path = Paths.get(files[i].getPath());
			ret.add(Files.readAllBytes(path));
		}
		return ret;
	}
	
	public ArrayList<String> getChunkNames() throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
		
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		for(int i=0; i<files.length; i++){
			
			ret.add(getFileId(files[i]));
		}
		return ret;
	}
	
	public String getFileId(File files){
		String[] split=files.getName().split("-");
		return split[0];
	}
	
	public ArrayList<String> getChunkNums() throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
		
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		for(int i=0; i<files.length; i++){
			
			ret.add(getFileNum(files[i]));
		}
		return ret;
	}
	
	public String getFileNum(File files){
		String[] split=files.getName().split("-");
		String[] ret=split[1].split(".bin");
		return ret[0];
	}
	
	public String getBestCandidate(ArrayList<String> chunkNames) throws IOException{
		
		ArrayList<Integer> repDegrees = new ArrayList<Integer>();
		ParseLog pl = new ParseLog();
		
		for(int i=0;i<chunkNames.size();i++){
			repDegrees.add(pl.countRepDegree(chunkNames.get(i), Integer.toString(i), serverId));
		}
		int index = getMax(repDegrees);
		return chunkNames.get(index);		
	}
	
	public int getBestIndex(ArrayList<String> chunkNames) throws IOException{
		ArrayList<Integer> repDegrees = new ArrayList<Integer>();
		ParseLog pl = new ParseLog();
		
		for(int i=0;i<chunkNames.size();i++){
			repDegrees.add(pl.countRepDegree(chunkNames.get(i), Integer.toString(i), serverId));
		}
		return getMax(repDegrees);
		
	}
	
	public int getNum(int index) throws IOException{
		return Integer.parseInt(getChunkNums().get(index));
	}
	
	public long getFileSize(String filename){
		long ret=0;
		
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		
		for(int i=0; i<files.length; i++){
			
			if(files[i].getName().equals(filename)){
				ret=files[i].length();
			}
		}
		return ret;
	}
	
	public int getMax(ArrayList<Integer> list){
		int ret=0;
		
		for(int i=0;i<list.size();i++){
			if(list.get(i) > ret){
				ret=i;
			}
		}
		
		return ret;
	}
	
	 
    public int[] getPos(ArrayList<String> files){
    	int[] chunkNums = new int[files.size()];
    	
    	for(int i=0;i<files.size();i++){
    		chunkNums[i]=Integer.parseInt(files.get(i));
    	}
    	
    	Arrays.sort(chunkNums);  
    	return chunkNums;
    }
}
