package protocols;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class History {

	public void add(String filename, String fileId, String chunkNum, String serverOrig, String messageType, String type) throws IOException{

		String name = "logs/history.txt";
		Files.write(Paths.get(name), (filename + " " + fileId + " " + chunkNum + " " + serverOrig + " " + messageType + " " + type + " " + "\n").getBytes(), StandardOpenOption.APPEND);
	}
}
