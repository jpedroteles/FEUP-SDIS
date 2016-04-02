package parser;

public class Chunk {
	private int chunkId;
	private byte[] content = new byte[64000];
	private int replicationDegree = 0;
	
	public int getChunkId() {
		return chunkId;
	}
	public void setChunkId(int chunkId) {
		this.chunkId = chunkId;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
	public int getReplicationDegree() {
		return replicationDegree;
	}
	public void setreplicationDegree(int num) {
		this.replicationDegree=num;
	}
}
