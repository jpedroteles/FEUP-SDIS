package parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;

public class Assemble {
	
	/*Construtor da classe assemble, esta classe trata de juntar os chunks de um ficheiro
	* @param  header  header da mensagem
	* @param  content conteudo do chunk
	* @param  senderId identificador do cliente
 	*/
	public Assemble(String header, byte[] content, String senderId) throws IOException {
		ParseMessage pm = new ParseMessage();
		//System.out.print("YES1");
		String fileId=pm.getId(header);
		String chunkNum = pm.getChunkNum(header);
		
		if(!senderId.equals(pm.getSenderId(header))){
			
			writeChunks(fileId, chunkNum, content, senderId);	
		}	
	}
	/*Faz a juncao dos chunk num  unico ficheiro
	* @param  hash  hash do chunk
	* @param  chunkNum numero do chunk
	* @param  content conteudo do chunk
	* @param senderId senderId identificador do cliente
 	*/	
	public void writeChunks(String hash, String chunkNum, byte[] content, String senderId) throws IOException{
		
		ParseLog pl = new ParseLog();
		File file = new File("restoredFiles/"+senderId+"-"+pl.getFile(hash));
		if(!file.exists()) {
			file.createNewFile();
		}
		FileOutputStream out = new FileOutputStream(file,true);
		byte[] c = new byte[getSize(content)];
		System.arraycopy(content, 0, c, 0, c.length);
		out.write(c);
	}
	/*Retorna o tamanho do chunk
	* @param  content  conteudo do chunk
	* @return um inteiro com a dimensao do chunk
 	*/	
	public int getSize(byte[] content){
		int ret=0;
		for(int i=0;i<content.length;i++){
			if(content[i]!=0){
				ret++;
			}
		}
		return ret;
	}
}
