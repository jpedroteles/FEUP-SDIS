package parser;

public class Chunk {
	/** Idenficador de um chunk */
	private int chunkId;
	/** Conteudo de um chunk */
	private byte[] content = new byte[64000];
	/** Numero de copias de um chunk */
	private int replicationDegree = 0;

	/** Retorna o id do chunk
	* @return um inteiro com o identificador de um chunk
 	*/		
	public int getChunkId() {
		return chunkId;
	}
	
	/** Define o identificador de um determinado chunk
	* @param chunkId identificador a atribuir ao chunk
 	*/	
	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}
	
	/** Retorna o conteudo de um chunk
	* @return byte[] com o conteudo de um chunk
 	*/		
	public byte[] getContent() {
		return content;
	}
	
	/** Define o conteudo de um determinado chunk
	* @param content conteudo a atribuir ao chunk
 	*/
	public void setContent(byte[] content) {
		this.content = content;
	}
	/** Retorna o numero de copias
	* @return um inteiro com o numero de copias do chunk
 	*/	
	public int getReplicationDegree() {
		return replicationDegree;
	}
	
	/** Define o numero de copia um determinado chunk
	* @param num numero de copias a atribuir ao chunk
 	*/	
	public void setreplicationDegree(int num) {
		this.replicationDegree=num;
	}
}
