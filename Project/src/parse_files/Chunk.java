package parse_files;

public class Chunk {
	private int chunkId;
	private String content;
	private int replicationDegree = 0;
	
	public int getChunkId() {
		return chunkId;
	}
	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getReplicationDegree() {
		return replicationDegree;
	}
	public void setreplicationDegree(int num) {
		this.replicationDegree=num;
	}
}
