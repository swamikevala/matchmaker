package org.ishafoundation.phosphorus.blocking;

package org.ishafoundation.phosphorus.blocking;

public class BlockLink {

	private int blockId;
	private boolean exactKeyMatch;
	
	public BlockLink (int blockId, boolean exactKeyMatch) {
		this.blockId = blockId;
		this.exactKeyMatch = exactKeyMatch;
	}
	
	public int getBlockId() {
		return id;
	}
	
	public boolean isExactKeyMatch() {
		return exactKeyMatch;
	}
	
}

