package protocols;

import java.io.File;

public class Utils {

	public void checkFolder() {

		File folder = new File("chunks");

		if (!folder.exists())
		{
			folder.mkdir();
			//System.out.println("FOLDER chunks CREATED");
		}
	}

	public boolean checkFile(String filename){
		
		return new File("chunks", filename).exists();
	}
}
