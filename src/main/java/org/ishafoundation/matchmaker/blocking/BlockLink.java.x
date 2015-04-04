package org.ishafoundation.matchmaker.blocking;

public class BlockLink {

	private String blockId;
	private boolean exactKeyMatch;
	
	public BlockLink (String blockId, boolean exactKeyMatch) {
		this.blockId = blockId;
		this.exactKeyMatch = exactKeyMatch;
	}
	
	public String getBlockId() {
		return blockId;
	}
	
	public boolean isExactKeyMatch() {
		return exactKeyMatch;
	}
	
}

