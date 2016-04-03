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
	/** Idenficador de um servidor */
	public static String serverId;
	
	/** Construtor da classe FileProcessor, esta classe tem todas as funcoes necessarias para o processamento de um ficheiro
	* @param  s  Identificador do servidor
 	*/
	public FileProcessor(String s){
		serverId=s;
	}

	/** Retorna o identificador de um determinado ficheiro
	* @param  filename  nome original de um ficheiro
 	*/
	public String get_fileId(String filename) throws CloneNotSupportedException, NoSuchAlgorithmException, UnsupportedEncodingException {
		
		File file = new File(filename);
		filename+=file.lastModified();
		
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(filename.getBytes("UTF-8"));
		byte[] fileId = md.digest();

		return DatatypeConverter.printHexBinary(fileId);
	}

	/** RDivide um ficheiro chunks de tamanho especifico
	* @param  filename  nome original de um ficheiro
	* @param maxSize tamanho maximo por chunk
 	*/
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

	/** Retorna um array list com todos os chunks
	* @return de um ArrayList com os respetivos chunks
 	*/
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

	/** Retorna um array list com todos os chunks names
	* @return de um ArrayList com respetivos nomes
 	*/	
	public ArrayList<String> getChunkNames() throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
		
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		for(int i=0; i<files.length; i++){
			
			ret.add(getFileId(files[i]));
		}
		return ret;
	}
	
	/** Retorna um identificador de um ficheiro
	* @return de um ArrayList com respetivos nomes
 	*/
	public String getFileId(File files){
		String[] split=files.getName().split("-");
		return split[0];
	}
	
	/** Retorna um array list com todos os numeros dos chunks
	* @return de um ArrayList com os respetivos numeros
 	*/
	public ArrayList<String> getChunkNums() throws IOException{
		ArrayList<String> ret = new ArrayList<String>();
		
		File folder = new File("chunks");
		File[] files = folder.listFiles();
		for(int i=0; i<files.length; i++){
			
			ret.add(getFileNum(files[i]));
		}
		return ret;
	}
	
	/** Retorna o numero de um ficheiro
	* @return de uma string com o numero do ficheiro
 	*/
	public String getFileNum(File files){
		String[] split=files.getName().split("-");
		String[] ret=split[1].split(".bin");
		return ret[0];
	}
	
	/** Retorna o melhor candidato para a realizacao da operacao pretendida
	* @param  chunkNames  arrayList como os nomes dos chunks
	* @return de uma string com o nome do melhor chunk
 	*/
	public String getBestCandidate(ArrayList<String> chunkNames) throws IOException{
		
		ArrayList<Integer> repDegrees = new ArrayList<Integer>();
		ParseLog pl = new ParseLog();
		
		for(int i=0;i<chunkNames.size();i++){
			repDegrees.add(pl.countRepDegree(chunkNames.get(i), Integer.toString(i), serverId));
		}
		int index = getMax(repDegrees);
		return chunkNames.get(index);		
	}
	
	/** Retorna o melhor indice de um array de chunkNames
	* @param  chunkNames  arrayList como os nomes dos chunks
	* @return do maximo do array list
 	*/
	public int getBestIndex(ArrayList<String> chunkNames) throws IOException{
		ArrayList<Integer> repDegrees = new ArrayList<Integer>();
		ParseLog pl = new ParseLog();
		
		for(int i=0;i<chunkNames.size();i++){
			repDegrees.add(pl.countRepDegree(chunkNames.get(i), Integer.toString(i), serverId));
		}
		return getMax(repDegrees);
		
	}
	
	/** Retorna o numero de um chunk
	* @return de um inteiro com o respetivo numero
 	*/
	public int getNum(int index) throws IOException{
		return Integer.parseInt(getChunkNums().get(index));
	}
	
	/** Retorna o tamanho de um ficheiro
	* @param  filename  nome do ficheiro
	* @return de um long com a dimensao do ficheiro
 	*/
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
	
	/** Retorna o maximo de um arraylist
	* @param  list  array sobre o qual vai procurar o maximo
	* @return de um inteiro com o maximo do respetivo array
 	*/
	public int getMax(ArrayList<Integer> list){
		int ret=0;
		
		for(int i=0;i<list.size();i++){
			if(list.get(i) > ret){
				ret=i;
			}
		}
		
		return ret;
	}
	
	/** Retorna a array de inteiros com os numeros dos chunks ordenados 
	* @param  files  array a ordenar
	* @return de um array de inteiros ordenados
 	*/	 
    public int[] getPos(ArrayList<String> files){
    	int[] chunkNums = new int[files.size()];
    	
    	for(int i=0;i<files.size();i++){
    		chunkNums[i]=Integer.parseInt(files.get(i));
    	}
    	
    	Arrays.sort(chunkNums);  
    	return chunkNums;
    }
}
