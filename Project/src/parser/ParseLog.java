package parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class ParseLog {

	/** Retorna o nome de um determinado ficheiro
	* @param  line string onde esta o nome do ficheiro
	* @return de uma string com o respetivo nome
 	*/
	public String getFilename(String line){
		String[] split = line.split(" ");
		return split[0];
	}

	/** Retorna o identificador de um determinado ficheiro
	* @param  line string onde esta o identificador do ficheiro
	* @return de uma string com o respetivo identificador
 	*/
	public String getFileId(String line){
		String[] split = line.split(" ");
		return split[1];
	}

	/** Retorna o numero de um determinado chunk
	* @param  line string onde esta o numero do chunk
	* @return de uma string com o respetivo numero
 	*/
	public String getChunkNum(String line){
		String[] split = line.split(" ");
		return split[2];
	}

	/** Retorna o identificador do servidor
	* @param  line string onde esta o identificador do servidor
	* @return de uma string com o respetivo identificador
 	*/
	public String getServerId(String line){
		String[] split = line.split(" ");
		return split[3];
	}

	/** Retorna o tipo de mensagem
	* @param  line string onde esta o tipo de mensagem
	* @return de uma string com o respetivo tipo
 	*/
	public String getMessageType(String line){
		String[] split = line.split(" ");
		return split[4];
	}

	/** Retorna o tipo 
	* @param  line string onde esta o tipo
	* @return de uma string com o respetivo tipo
 	*/
	public String getType(String line){
		String[] split = line.split(" ");
		return split[5];
	}

	/** Retorna uma linha do log com o historico de operacoes
	* @param  line inteiro com o numero da linha
	* @return de uma string com a linha respetiva
 	*/
	public String getLine(int line) throws IOException{
		Stream<String> lines = Files.lines(Paths.get("logs/history.txt"));
		String ret = lines.skip(line-1).findFirst().get();
		return ret;
	}

	/** Retorna numero de copias para um ficheiro
	* @param  hash nome "hashado" do ficheiro
	* @param chunkNum numero de chunk
	* @param serverId identificador do servidor
	* @return de um inteiro com o numero de copias
 	*/
	public int countRepDegree(String hash, String chunkNum, String serverId) throws IOException{
		int ret=0;
		ArrayList<String> serverIds = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader("logs/history.txt"));
		String line;
		while((line = br.readLine()) != null){
			String fileId = getFileId(line);
			String chunk = getChunkNum(line);

			if(!serverIds.contains(getServerId(line))){

				if(fileId.equals(hash) && chunk.equals(chunkNum) && getMessageType(line).equals("STORED") && serverId.equals(getServerId(line))){
					ret++;
					serverIds.add(getServerId(line));
				}
				else if(fileId.equals(hash) && getMessageType(line).equals("DELETE") && serverId.equals(getServerId(line))){
					ret--;
					serverIds.remove(getServerId(line));
				}
				else if(fileId.equals(hash) && chunk.equals(chunkNum) && getMessageType(line).equals("REMOVED") && serverId.equals(getServerId(line))){
					ret--;
					serverIds.remove(getServerId(line));
				}
			}
		}

		if(ret < 0){
			ret=0;
		}
		return ret;
	}

	/** Retorna uma string com o nome do ficheiro
	* @param  hash string com a hash do ficheiro
	* @return de uma string com o respetivo nome
 	*/
	public String getFile(String hash) throws IOException{

		String ret = null;
		ArrayList<String> serverIds = new ArrayList<String>();

		BufferedReader br = new BufferedReader(new FileReader("logs/history.txt"));
		String line;
		while((line = br.readLine()) != null){
			String fileId = getFileId(line);

			if(fileId.equals(hash) && !getFilename(line).equals("-")){
				ret=getFilename(line);
				break;
			}
		}
		return ret;
	}
}
