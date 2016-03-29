package parser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.util.ArrayList;

import javax.xml.bind.DatatypeConverter;

public class FileProcessor {

	public String get_fileId(String filename) throws CloneNotSupportedException, NoSuchAlgorithmException, UnsupportedEncodingException {

		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(filename.getBytes("UTF-8"));
		byte[] fileId = md.digest();

		return DatatypeConverter.printHexBinary(fileId);
	}

	public ArrayList<byte[]> divide_in_chunks(String filename, int maxSize) {

		File inputFile = new File(filename);
		FileInputStream inputStream;
		int fileSize = (int) inputFile.length();
		//System.out.println("File Size: " + fileSize);
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
					readLength = fileSize;
				}
				chunkPart = new byte[readLength];
				read = inputStream.read(chunkPart,0,readLength);
				fileSize -= read;

				assert (read == chunkPart.length);

				ret.add(chunkPart);
				//System.out.println("New chunk added");
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
			//System.out.println(path);
			ret.add(Files.readAllBytes(path));
		}
		return ret;
	}
	
	public ArrayList<String> getChunkNames() throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
		
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		//System.out.println("LENGTH: " + files.length);
		for(int i=0; i<files.length; i++){
			
			//System.out.println("NAME:" + getFileId(files[i]));
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
		//System.out.println("LENGTH: " + files.length);
		for(int i=0; i<files.length; i++){
			
			//System.out.println("NAME:" + getFileId(files[i]));
			ret.add(getFileNum(files[i]));
		}
		return ret;
	}
	
	public String getFileNum(File files){
		String[] split=files.getName().split("-");
		System.out.println(split[1]);
		String[] ret=split[1].split(".bin");
		return ret[0];
	}
}
