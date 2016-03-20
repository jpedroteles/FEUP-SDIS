package parse_files;

import java.util.ArrayList;

public class SingleFile {

	private String fileId;
	private ArrayList<Chunk> chunks = new ArrayList<Chunk>();
	private int replicationDegree;
	
	public String getFileId() {
		return fileId;
	}
	
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}
	
	public ArrayList<Chunk> getChunks() {
		return chunks;
	}

	public void setChunks(ArrayList<Chunk> chunks) {
		this.chunks = chunks;
	}

	public void addChunk(String chunk)
	{
		Chunk c = new Chunk();
		c.setChunkId(this.chunks.size()+1);
		c.setContent(chunk);
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public void setReplicationDegree(int replicationDegree) {
		this.replicationDegree = replicationDegree;
		
		for(int i=0; i<chunks.size();i++) {
			chunks.get(i).setreplicationDegree(replicationDegree);
		}
	}
	
	
}

