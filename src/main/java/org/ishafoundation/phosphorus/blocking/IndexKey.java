package org.ishafoundation.phosphorus.blocking;

import org.ishafoundation.phosphorus.blocking.BaseKey;
import org.ishafoundation.phosphorus.blocking.BlockLink;

public class IndexKey {

	private String value;
	private BaseKey baseKey;
	private int id;
	private Set<BlockLink> blockLinks;
	
	public IndexKey(String value, BaseKey baseKey) {
		this.value = value;
		this.baseKey = baseKey;
		this.id = 0;
		this.blockLinks = new Set<BlockLink>();
	}
	
	public String getValue() {
		return value;
	}
	
	public int getLength() {
		return value.length();
	}
	
	public String getBaseKey() {
		return baseKey;
	}
	
	public int getId() {
		return id;
	}
	
	public Set<BlockLink> getBlockLinks() {
		return blockLinks;
	}
	
	public void addBlockLink(BlockLink newLink) {
		this.blockLinks.add(newLink);
	}
	
	public void removeBlockLink(BlockLink link) {
		this.blockLinks.remove(link);
	}
	
}

