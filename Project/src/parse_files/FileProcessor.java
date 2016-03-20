package parse_files;

import java.io.*;
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

	public ArrayList<String> get_chunks(String filename, int maxSize) {
		
		File inputFile = new File(filename);
		FileInputStream inputStream;
		int fileSize = (int) inputFile.length();
		System.out.println("File Size: " + fileSize);
		int read=0, readLength = maxSize;
		byte[] chunkPart;
		boolean addLastChunk=false;
		ArrayList<String> ret = new ArrayList<String>();
		
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
				
				ret.add(new String(chunkPart));
				System.out.println("New chunk added");
				chunkPart=null;
			}
			inputStream.close();
			
		} catch (IOException exception){
			exception.printStackTrace();
		}
		
		if(addLastChunk) {
			String s = new String();
			ret.add(s);
		}
		return ret;
	}
}
