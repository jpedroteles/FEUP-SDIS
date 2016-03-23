package parser;

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
	
	public void write_chunks(SingleFile file) throws IOException {
		
		for(int i=0;i<file.getChunks().size();i++) {

			String name = "chunks/"+file.getFileId()+"-"+i+".bin";
			System.out.println(name);
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(name), "utf-8"));
			writer.write(new String(file.getChunks().get(i).getContent()));
		}
	}
	
	public void create_chunk_folder() {
		File dir = new File("chunks");
		dir.mkdir();
	}
}
