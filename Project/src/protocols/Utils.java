package protocols;

import java.io.File;

public class Utils {

	/** Verifica se existe uma pasta chamada chunks se nao existir cria uma
	*/
	public void checkFolder() {

		File folder = new File("chunks");

		if (!folder.exists())
		{
			folder.mkdir();
		}
	}
	/** Verifica se existe um ficheiro com o nome de filename 
	* @param filename nome do ficheiro
	* @return true se encontrou o ficheiro false se nao
	*/
	public boolean checkFile(String filename){
		
		return new File("chunks", filename).exists();
	}
}
