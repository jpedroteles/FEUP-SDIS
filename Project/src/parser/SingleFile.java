package parser;

import java.util.ArrayList;

public class SingleFile {

	/** Identificador de um ficheiro */
	private String fileId;
	/** Array de chunks */
	private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	/** Numero de copias */
	private int replicationDegree;
	
	/** Retorna o identificador de um determinado ficheiro
	* @return de uma string com o respetivo identificador
 	*/
	public String getFileId() {
		return fileId;
	}
	
	/** Define o identificador de um determinado ficheiro
	* @param fileId identificador a atribuir ao ficheiro
 	*/	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	/** Retorna um arrayList com chunks
	* @return de um array com os chunks
 	*/
	public ArrayList<Chunk> getChunks() {
		return chunks;
	}

	/** Define um arrayList com chunks
	* @param array com os chunks
 	*/
	public void setChunks(ArrayList<Chunk> chunks) {
		this.chunks = chunks;
	}

	/** Adiciona um chunk
	* @param chunk chunk a adicionar
 	*/
	public void addChunk(byte[] chunk)
	{
		Chunk c = new Chunk();
		c.setChunkId(this.chunks.size()+1);
		c.setContent(chunk);
		chunks.add(c);
	}

	/** Retorna o numero de copia
	* @return inteiro com o numero de copias
 	*/
	public int getReplicationDegree() {
		return replicationDegree;
	}

	/** Define o numero de copia
	* @param replicationDegree numero de copias a atribuir
 	*/
	public void setReplicationDegree(int replicationDegree) {
		this.replicationDegree = replicationDegree;
		
		for(int i=0; i<chunks.size();i++) {
			chunks.get(i).setreplicationDegree(replicationDegree);
		}
	}
	
	
}

