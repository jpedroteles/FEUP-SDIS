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

	public String getFilename(String line){
		String[] split = line.split(" ");
		return split[0];
	}

	public String getFileId(String line){
		String[] split = line.split(" ");
		return split[1];
	}

	public String getChunkNum(String line){
		String[] split = line.split(" ");
		return split[2];
	}

	public String getServerId(String line){
		String[] split = line.split(" ");
		return split[3];
	}

	public String getMessageType(String line){
		String[] split = line.split(" ");
		return split[4];
	}

	public String getType(String line){
		String[] split = line.split(" ");
		return split[5];
	}

	public String getLine(int line) throws IOException{
		Stream<String> lines = Files.lines(Paths.get("logs/history.txt"));
		String ret = lines.skip(line-1).findFirst().get();
		return ret;
	}

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
